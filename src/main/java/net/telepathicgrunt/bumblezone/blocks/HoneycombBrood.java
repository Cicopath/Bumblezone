package net.telepathicgrunt.bumblezone.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.telepathicgrunt.bumblezone.Bumblezone;
import net.telepathicgrunt.bumblezone.effects.BzEffects;
import net.telepathicgrunt.bumblezone.entities.BzEntities;
import net.telepathicgrunt.bumblezone.items.BzItems;

import java.util.List;
import java.util.Random;


public class HoneycombBrood extends FacingBlock {
    public static final IntProperty STAGE = Properties.AGE_3;
    private static final TargetPredicate FIXED_DISTANCE = (new TargetPredicate()).setBaseMaxDistance(50);
    private static final TargetPredicate PLAYER_DISTANCE = (new TargetPredicate());

    public HoneycombBrood() {
        super(FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MaterialColor.ORANGE).ticksRandomly().strength(0.5F, 0.5F).sounds(BlockSoundGroup.CORAL).build().velocityMultiplier(0.9F));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(STAGE, 0));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE);
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getSide().getOpposite());
    }


    @Override
    public BlockState rotate(BlockState state, BlockRotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }


    @Override
    public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.get(FACING)));
    }


    /**
     * Called when the given entity walks on this Block
     */
    @Override
    public void onSteppedOn(World worldIn, BlockPos pos, Entity entityIn) {
        double yMagnitude = Math.abs(entityIn.getVelocity().y);
        if (yMagnitude < 0.1D) {
            double slowFactor = 0.85D;
            entityIn.setVelocity(entityIn.getVelocity().multiply(slowFactor, 1.0D, slowFactor));
        }

        super.onSteppedOn(worldIn, pos, entityIn);
    }


    /**
     * Allow player to harvest honey and put honey into this block using bottles or wands
     */
    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState thisBlockState, World world, BlockPos position, PlayerEntity playerEntity, Hand playerHand, BlockHitResult raytraceResult) {
        ItemStack itemstack = playerEntity.getStackInHand(playerHand);

        //VANILLA COMPAT
        /*
         * Player is taking honey and killing larva
         */
        if (itemstack.getItem() == Items.GLASS_BOTTLE) {
            world.setBlockState(position, BzBlocks.EMPTY_HONEYCOMB_BROOD.getDefaultState().with(Properties.FACING, thisBlockState.get(Properties.FACING)), 3); // removed honey from this block

            //spawn angry bee if at final stage and front isn't blocked off
            int stage = thisBlockState.get(STAGE);
            spawnBroodMob(world, thisBlockState, position, stage);
            world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            if (!playerEntity.isCreative()) {
                itemstack.decrement(1); // remove current empty bottle

                if (itemstack.isEmpty()) {
                    playerEntity.setStackInHand(playerHand, new ItemStack(Items.HONEY_BOTTLE)); // places honey bottle in hand
                } else if (!playerEntity.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) // places honey bottle in inventory
                {
                    playerEntity.dropItem(new ItemStack(Items.HONEY_BOTTLE), false); // drops honey bottle if inventory is full
                }
            }

            if ((playerEntity.getEntityWorld().getRegistryKey().getValue().equals(Bumblezone.MOD_DIMENSION_ID) ||
                    Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.allowWrathOfTheHiveOutsideBumblezone) &&
                    !playerEntity.isCreative() &&
                    !playerEntity.isSpectator() &&
                    Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressiveBees)
            {
                if(playerEntity.hasStatusEffect(BzEffects.PROTECTION_OF_THE_HIVE)){
                    playerEntity.removeStatusEffect(BzEffects.PROTECTION_OF_THE_HIVE);
                }
                else {
                    //Now all bees nearby in Bumblezone will get VERY angry!!!
                    playerEntity.addStatusEffect(new StatusEffectInstance(BzEffects.WRATH_OF_THE_HIVE, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.howLongWrathOfTheHiveLasts, 2, false, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.showWrathOfTheHiveParticles, true));
                }
            }

            return ActionResult.SUCCESS;
        }
        /*
         * Player is feeding larva
         */
        else if (itemstack.getItem() == Items.HONEY_BOTTLE || itemstack.getItem() == BzItems.SUGAR_WATER_BOTTLE) {
            if (!world.isClient) {
                boolean successfulGrowth = false;

                //chance of growing the larva
                if (itemstack.getItem() == BzItems.SUGAR_WATER_BOTTLE) {
                    if (world.random.nextFloat() < 0.30F)
                        successfulGrowth = true;
                } else {
                    successfulGrowth = true;
                }

                if (successfulGrowth && world.random.nextFloat() < 0.30F) {
                    if(!playerEntity.hasStatusEffect(BzEffects.WRATH_OF_THE_HIVE)){
                        playerEntity.addStatusEffect(new StatusEffectInstance(BzEffects.PROTECTION_OF_THE_HIVE, (int) (Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.howLongProtectionOfTheHiveLasts * 0.75f), 1, false, false,  true));
                    }
                }

                //grows larva
                if (successfulGrowth) {
                    //spawn bee if at final stage and front isn't blocked off
                    int stage = thisBlockState.get(STAGE);
                    if (stage == 3) {
                        spawnBroodMob(world, thisBlockState, position, stage);
                    } else {
                        world.setBlockState(position, thisBlockState.with(STAGE, stage + 1));
                    }
                }
            }

            //block grew one stage or bee was spawned
            world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            //removes used item
            if (!playerEntity.isCreative()) {
                itemstack.decrement(1); // remove current honey bottle

                if (itemstack.isEmpty()) {
                    playerEntity.setStackInHand(playerHand, new ItemStack(Items.GLASS_BOTTLE)); // places empty bottle in hand
                } else if (!playerEntity.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE))) // places empty bottle in inventory
                {
                    playerEntity.dropItem(new ItemStack(Items.GLASS_BOTTLE), false); // drops empty bottle if inventory is full
                }
            }

            return ActionResult.SUCCESS;
        }

        return super.onUse(thisBlockState, world, position, playerEntity, playerHand, raytraceResult);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos position, Random rand) {
        super.scheduledTick(state, world, position, rand);
        if (!world.isRegionLoaded(position, position))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light

        int stage = state.get(STAGE);
        if (stage < 3) {
            if (world.getRegistryKey().getValue().equals(Bumblezone.MOD_DIMENSION_ID) ? rand.nextInt(10) == 0 : rand.nextInt(22) == 0) {
                world.setBlockState(position, state.with(STAGE, stage + 1), 2);
            }
        } else {
            PLAYER_DISTANCE.setBaseMaxDistance(Math.max(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius * 0.5, 1));

            List<BeeEntity> beeList = world.getTargets(BeeEntity.class, FIXED_DISTANCE, null, new Box(position).expand(50));
            List<PlayerEntity> playerList = world.getTargets(PlayerEntity.class, PLAYER_DISTANCE, null, new Box(position).expand(50));
            if (beeList.size() < 3 || playerList.stream().anyMatch(player -> player.hasStatusEffect(BzEffects.WRATH_OF_THE_HIVE))) {
                spawnBroodMob(world, state, position, stage);
            }
        }
    }


    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
     * this block
     */
    @Override
    public void onBreak(World world, BlockPos position, BlockState state, PlayerEntity playerEntity) {

        Hand hand = playerEntity.getActiveHand();
        if (hand != null) {
            ListTag listOfEnchants = playerEntity.getStackInHand(hand).getEnchantments();
            if (listOfEnchants.stream().noneMatch(enchant -> enchant.asString().contains("minecraft:silk_touch"))) {
                BlockState blockState = world.getBlockState(position);
                int stage = blockState.get(STAGE);
                if (stage == 3) {
                    spawnBroodMob(world, blockState, position, stage);
                }

                if ((playerEntity.getEntityWorld().getRegistryKey().getValue().equals(Bumblezone.MOD_DIMENSION_ID) ||
                        Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.allowWrathOfTheHiveOutsideBumblezone) &&
                        !playerEntity.isCreative() &&
                        !playerEntity.isSpectator() &&
                        Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressiveBees)
                {
                    if(playerEntity.hasStatusEffect(BzEffects.PROTECTION_OF_THE_HIVE)){
                        playerEntity.removeStatusEffect(BzEffects.PROTECTION_OF_THE_HIVE);
                    }
                    else{
                        //Now all bees nearby in Bumblezone will get VERY angry!!!
                        playerEntity.addStatusEffect(new StatusEffectInstance(BzEffects.WRATH_OF_THE_HIVE, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.howLongWrathOfTheHiveLasts, 2, false, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.showWrathOfTheHiveParticles, true));
                    }
                }
            }
        }

        super.onBreak(world, position, state, playerEntity);
    }


    private static void spawnBroodMob(World world, BlockState state, BlockPos position, int stage) {
        //the front of the block
        BlockPos.Mutable blockpos = new BlockPos.Mutable().set(position);
        blockpos.move(state.get(FACING).getOpposite());

        if (stage == 3 && !world.getBlockState(blockpos).getMaterial().isSolid()) {
            MobEntity beeMob = EntityType.BEE.create(world);

            spawnMob(world, blockpos, beeMob, beeMob);

            if(world.random.nextFloat() < 0.1f){
                MobEntity honeySlimeMob = BzEntities.HONEY_SLIME.create(world);
                spawnMob(world, blockpos, beeMob, honeySlimeMob);
            }

            world.setBlockState(position, state.with(STAGE, 0));

        }

    }

    private static void spawnMob(World world, BlockPos.Mutable blockpos, MobEntity beeMob, MobEntity entity) {
        if(entity == null || world.isClient) return;
        entity.refreshPositionAndAngles(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, world.getRandom().nextFloat() * 360.0F, 0.0F);
        entity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(new BlockPos(beeMob.getPos())), SpawnReason.TRIGGERED, null, null);
        world.spawnEntity(entity);
    }


    /**
     * tell redstone that this can be use with comparator
     */
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }


    /**
     * the power fed into comparator (1 - 4)
     */
    @Override
    public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
        return blockState.get(STAGE) + 1;
    }


    /**
     * Called periodically clientside on blocks near the player to show honey particles. 50% of attempting to spawn a
     * particle. Also will buzz too based on stage
     */
    @Override
    public void randomDisplayTick(BlockState blockState, World world, BlockPos position, Random random) {
        //number of particles in this tick
        for (int i = 0; i < random.nextInt(2); ++i) {
            this.spawnHoneyParticles(world, position, blockState);
        }

        int stage = blockState.get(STAGE);
        float soundVolume = 0.05F + stage * 0.1F;
        if (world.random.nextInt(20) == 0)
            world.playSound(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, SoundEvents.ENTITY_BEE_LOOP, SoundCategory.BLOCKS, soundVolume, 1.0F, true);
    }


    /**
     * Starts checking if the block can take the particle and if so and it passes another rng to reduce spawnrate, it then
     * takes the block's dimensions and passes into methods to spawn the actual particle
     */
    private void spawnHoneyParticles(World world, BlockPos position, BlockState blockState) {
        if (blockState.getFluidState().isEmpty() && world.random.nextFloat() < 0.08F) {
            VoxelShape currentBlockShape = blockState.getCollisionShape(world, position);
            double yEndHeight = currentBlockShape.getMax(Direction.Axis.Y);
            if (yEndHeight >= 1.0D && !blockState.isIn(BlockTags.IMPERMEABLE)) {
                double yStartHeight = currentBlockShape.getMin(Direction.Axis.Y);
                if (yStartHeight > 0.0D) {
                    this.addHoneyParticle(world, position, currentBlockShape, position.getY() + yStartHeight - 0.05D);
                } else {
                    BlockPos belowBlockpos = position.down();
                    BlockState belowBlockstate = world.getBlockState(belowBlockpos);
                    VoxelShape belowBlockShape = belowBlockstate.getCollisionShape(world, belowBlockpos);
                    double yEndHeight2 = belowBlockShape.getMax(Direction.Axis.Y);
                    if ((yEndHeight2 < 1.0D || !belowBlockstate.isOpaqueFullCube(world, belowBlockpos)) && belowBlockstate.getFluidState().isEmpty()) {
                        this.addHoneyParticle(world, position, currentBlockShape, position.getY() - 0.05D);
                    }
                }
            }

        }
    }


    /**
     * intermediary method to apply the blockshape and ranges that the particle can spawn in for the next addHoneyParticle
     * method
     */
    private void addHoneyParticle(World world, BlockPos blockPos, VoxelShape blockShape, double height) {
        this.addHoneyParticle(
                world,
                blockPos.getX() + blockShape.getMin(Direction.Axis.X),
                blockPos.getX() + blockShape.getMax(Direction.Axis.X),
                blockPos.getZ() + blockShape.getMin(Direction.Axis.Z),
                blockPos.getZ() + blockShape.getMax(Direction.Axis.Z),
                height);
    }


    /**
     * Adds the actual honey particle into the world within the given range
     */
    private void addHoneyParticle(World world, double xMin, double xMax, double zMax, double zMin, double yHeight) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), xMin, xMax), yHeight, MathHelper.lerp(world.random.nextDouble(), zMax, zMin), 0.0D, 0.0D, 0.0D);
    }
}

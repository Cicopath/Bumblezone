package com.telepathicgrunt.the_bumblezone.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.items.HoneyBeeLeggings;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HoneyWeb extends Block {

    public static final BooleanProperty NORTHSOUTH = BooleanProperty.create("northsouth");
    public static final BooleanProperty EASTWEST = BooleanProperty.create("eastwest");
    public static final BooleanProperty UPDOWN = BooleanProperty.create("updown");
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap<>();
    public static final Map<Direction.Axis, BooleanProperty> AXIS_TO_PROP = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.Axis.class), (map) -> {
        map.put(Direction.Axis.X, NORTHSOUTH);
        map.put(Direction.Axis.Z, EASTWEST);
        map.put(Direction.Axis.Y, UPDOWN);
    }));

    public static final MapCodec<HoneyWeb> CODEC = Block.simpleCodec(HoneyWeb::new);

    public HoneyWeb() {
        this(Properties.of()
                .mapColor(MapColor.TERRACOTTA_ORANGE)
                .forceSolidOn()
                .noOcclusion()
                .noCollission()
                .requiresCorrectToolForDrops()
                .strength(4.0F)
                .pushReaction(PushReaction.DESTROY));
    }

    public HoneyWeb(Properties properties) {
        super(properties);

        this.collisionShapeByIndex = this.makeShapes();
        this.shapeByIndex = this.makeShapes();
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTHSOUTH, false)
                .setValue(EASTWEST, false)
                .setValue(UPDOWN, false));
    }

    @Override
    public MapCodec<? extends HoneyWeb> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(NORTHSOUTH, EASTWEST, UPDOWN);
    }

    protected VoxelShape[] makeShapes() {
        VoxelShape voxelshape1 = Block.box(7, 0, 0, 9, 16, 16);
        VoxelShape voxelshape2 = Block.box(0, 0, 7, 16, 16, 9);
        VoxelShape voxelshape3 = Block.box(0, 7, 0, 16, 9, 16);

        return new VoxelShape[]{
                Shapes.empty(),
                voxelshape1,
                voxelshape2,
                Shapes.or(voxelshape1, voxelshape2),
                voxelshape3,
                Shapes.or(voxelshape1, voxelshape3),
                Shapes.or(voxelshape2, voxelshape3),
                Shapes.joinUnoptimized(Shapes.joinUnoptimized(voxelshape1, voxelshape2, BooleanOp.OR), voxelshape3, BooleanOp.OR).optimize()
        };
    }

    private static int getNewBitFlag(BlockState blockState) {
        int bitFlag = 0;
        if (blockState.getValue(NORTHSOUTH)) {
            bitFlag |= 1;
        }

        if (blockState.getValue(EASTWEST)) {
            bitFlag |= (1 << 1);
        }

        if (blockState.getValue(UPDOWN)) {
            bitFlag |= (1 << 2);
        }

        return bitFlag;
    }

    protected int getAABBIndex(BlockState blockState) {
        return this.stateToIndex.computeIfAbsent(blockState, HoneyWeb::getNewBitFlag);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapeByIndex[this.getAABBIndex(blockState)];
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity.getType().is(BzTags.HONEY_WEB_CANNOT_SLOW)) {
            super.entityInside(blockState, level, blockPos, entity);
            return;
        }

        if (!(entity instanceof Player player && player.isCreative())) {
            ItemStack beeLeggings = entity instanceof LivingEntity livingEntity ? HoneyBeeLeggings.getEntityBeeLegging(livingEntity) : ItemStack.EMPTY;

            VoxelShape shape = this.shapeByIndex[this.getAABBIndex(blockState)];
            shape = shape.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (Shapes.joinIsNotEmpty(shape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND)) {
                if(entity instanceof Projectile) {
                    double speedReduction = 0.015f;
                    Vec3 deltaMovement = entity.getDeltaMovement();
                    double magnitude = deltaMovement.length();
                    if(magnitude != 0) {
                        speedReduction = speedReduction / magnitude;
                        entity.makeStuckInBlock(blockState, new Vec3(speedReduction, speedReduction, speedReduction));
                    }
                }
                else {
                    double speedReduction = 0.1f;
                    if(!beeLeggings.isEmpty()) {
                        speedReduction = 0.9f;
                    }
                    else {
                        Vec3 deltaMovement = entity.getDeltaMovement();
                        double magnitude = deltaMovement.length();
                        if(magnitude != 0) {
                            speedReduction = speedReduction / magnitude;
                        }
                    }
                    entity.makeStuckInBlock(blockState, new Vec3(speedReduction, speedReduction, speedReduction));
                }

                if (beeLeggings.isEmpty() && entity instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SLOWDOWN,
                            200,
                            1,
                            false,
                            false,
                            true));
                }
            }
        }
        super.entityInside(blockState, level, blockPos, entity);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Level level = placeContext.getLevel();
        BlockPos blockpos = placeContext.getClickedPos();
        BlockState state = getStateForSpot(level, blockpos);
        if(!state.getValue(NORTHSOUTH) && !state.getValue(EASTWEST) && !state.getValue(UPDOWN)) {
            if(placeContext.getClickedFace().getAxis() != Direction.Axis.Y) {
                state = this.defaultBlockState().setValue(UPDOWN, true);
            }
            else {
                Direction.Axis horizontalFacing = Direction.Axis.Z;
                for(Direction direction : placeContext.getNearestLookingDirections()) {
                    if(direction.getAxis() != Direction.Axis.Y) {
                        horizontalFacing = direction.getAxis();
                        break;
                    }
                }

                if(horizontalFacing == Direction.Axis.X) {
                    state = this.defaultBlockState()
                            .setValue(NORTHSOUTH, true)
                            .setValue(EASTWEST, false);
                }
                else {
                    state = this.defaultBlockState()
                            .setValue(NORTHSOUTH, false)
                            .setValue(EASTWEST, true);
                }
            }
        }

        updateNeighboringStates(level, state, blockpos);
        return state;
    }

    /**
     * Allow player to remove this block with water buckets, water bottles, or wet sponges
     */
    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos position, Player playerEntity, InteractionHand playerHand, BlockHitResult raytraceResult) {
        if (itemStack.is(BzTags.WASHING_ITEMS) &&
            (!itemStack.is(Items.POTION) ||
            (itemStack.getComponents().has(DataComponents.POTION_CONTENTS) && itemStack.getComponents().get(DataComponents.POTION_CONTENTS).is(Potions.WATER))))
        {

            if (!itemStack.isEmpty()) {
                playerEntity.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            }

            if (playerEntity instanceof ServerPlayer serverPlayer) {
                BzCriterias.CLEANUP_HONEY_WEB_TRIGGER.get().trigger(serverPlayer);
            }

            level.destroyBlock(position, false);

            level.playSound(
                    playerEntity,
                    playerEntity.getX(),
                    playerEntity.getY(),
                    playerEntity.getZ(),
                    BzSounds.WASHING_RESIDUES.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);

            if (level.isClientSide()) {
                for (int i = 0; i < 25; ++i) {
                    this.addParticle(
                            ParticleTypes.FALLING_WATER,
                            level,
                            playerEntity.getRandom(),
                            position,
                            blockState.getShape(level, position));
                }
            }

            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(itemStack, blockState, level, position, playerEntity, playerHand, raytraceResult);
    }

    private void updateNeighboringStates(Level level, BlockState centerState, BlockPos blockpos) {
        for (Direction direction : Direction.values()) {
            BlockPos sidePos = blockpos.relative(direction);
            BlockState neighboringBlockstate = level.getBlockState(sidePos);
            if (neighboringBlockstate.getBlock() instanceof HoneyWeb) {
                boolean changedState = false;
                if (direction.getAxis() != Direction.Axis.X && centerState.getValue(NORTHSOUTH) && !neighboringBlockstate.getValue(NORTHSOUTH)) {
                    neighboringBlockstate = neighboringBlockstate.setValue(NORTHSOUTH, true);
                    changedState = true;
                }
                if (direction.getAxis() != Direction.Axis.Z && centerState.getValue(EASTWEST) && !neighboringBlockstate.getValue(EASTWEST)) {
                    neighboringBlockstate = neighboringBlockstate.setValue(EASTWEST, true);
                    changedState = true;
                }
                if (direction.getAxis() != Direction.Axis.Y && centerState.getValue(UPDOWN) && !neighboringBlockstate.getValue(UPDOWN)) {
                    neighboringBlockstate = neighboringBlockstate.setValue(UPDOWN, true);
                    changedState = true;
                }

                if (changedState) {
                    level.setBlock(sidePos, neighboringBlockstate, 3);
                }
            }
        }
    }

    @NotNull
    private BlockState getStateForSpot(BlockGetter blockgetter, BlockPos blockpos) {
        BlockState currentBlockstate = this.defaultBlockState();

        for (Direction direction : Direction.values()) {
            BlockState neighboringBlockstate = blockgetter.getBlockState(blockpos.relative(direction));
            if (neighboringBlockstate.getBlock() instanceof HoneyWeb) {
                if (direction.getAxis() == Direction.Axis.Y) {
                    if (neighboringBlockstate.getValue(NORTHSOUTH)) {
                        currentBlockstate = currentBlockstate.setValue(NORTHSOUTH, true);
                    }
                    if (neighboringBlockstate.getValue(EASTWEST)) {
                        currentBlockstate = currentBlockstate.setValue(EASTWEST, true);
                    }
                }
                else if (direction.getAxis() == Direction.Axis.X) {
                    if (neighboringBlockstate.getValue(UPDOWN)) {
                        currentBlockstate = currentBlockstate.setValue(UPDOWN, true);
                    }
                    if (neighboringBlockstate.getValue(EASTWEST)) {
                        currentBlockstate = currentBlockstate.setValue(EASTWEST, true);
                    }
                }
                else if (direction.getAxis() == Direction.Axis.Z) {
                    if (neighboringBlockstate.getValue(UPDOWN)) {
                        currentBlockstate = currentBlockstate.setValue(UPDOWN, true);
                    }
                    if (neighboringBlockstate.getValue(NORTHSOUTH)) {
                        currentBlockstate = currentBlockstate.setValue(NORTHSOUTH, true);
                    }
                }
            }
        }

        return currentBlockstate;
    }


    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> blockState.setValue(NORTHSOUTH, blockState.getValue(EASTWEST)).setValue(EASTWEST, blockState.getValue(NORTHSOUTH));
            default -> blockState;
        };
    }

    /**
     * Called periodically clientside on blocks near the player to show honey particles
     */
    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos position, RandomSource random) {
        //chance of particle in this tick
        for (int i = 0; i == random.nextInt(50); ++i) {
            this.addParticle(ParticleTypes.DRIPPING_HONEY, world, random, position, blockState.getShape(world, position));
        }
    }

    /**
     * intermediary method to apply the blockshape and ranges that the particle can spawn in for the next addParticle method
     */
    protected void addParticle(ParticleOptions particleType, Level world, RandomSource random, BlockPos blockPos, VoxelShape blockShape) {
        this.addParticle(
                particleType,
                world,
                random,
                blockPos.getX() + blockShape.min(Direction.Axis.X),
                blockPos.getX() + blockShape.max(Direction.Axis.X),
                blockPos.getY() + blockShape.min(Direction.Axis.Y),
                blockPos.getY() + blockShape.max(Direction.Axis.Y),
                blockPos.getZ() + blockShape.min(Direction.Axis.Z),
                blockPos.getZ() + blockShape.max(Direction.Axis.Z));
    }

    /**
     * Adds the actual particle into the world within the given range
     */
    private void addParticle(ParticleOptions particleType, Level world, RandomSource random, double xMin, double xMax, double yMin, double yMax, double zMax, double zMin) {
        world.addParticle(particleType, Mth.lerp(random.nextDouble(), xMin, xMax), Mth.lerp(random.nextDouble(), yMin, yMax), Mth.lerp(random.nextDouble(), zMin, zMax), 0.0D, 0.0D, 0.0D);
    }
}

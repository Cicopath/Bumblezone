package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.blocks.blockentities.PotionCandleBlockEntity;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlockEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.platform.BlockExtension;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class PotionCandleBase extends BaseEntityBlock implements SimpleWaterloggedBlock, SuperCandle, BlockExtension {
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);

    public static final MapCodec<PotionCandleBase> CODEC = Block.simpleCodec(PotionCandleBase::new);

    public PotionCandleBase() {
        this(Properties.of()
                .mapColor(MapColor.SAND)
                .lightLevel((blockState) -> blockState.getValue(LIT) ? 15 : 0)
                .noOcclusion()
                .strength(0.1F)
                .sound(SoundType.CANDLE)
                .pushReaction(PushReaction.DESTROY));
    }

    public PotionCandleBase(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public MapCodec<? extends PotionCandleBase> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        SuperCandle.placeWickIfPossible(level, pos, false);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.is(FluidTags.WATER) && fluidstate.isSource();
        return super.getStateForPlacement(context).setValue(WATERLOGGED, flag);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        SuperCandle.placeWickIfPossible(world, pos, false);
        super.neighborChanged(blockstate, world, pos, block, fromPos, notify);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter world, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return !blockState.getValue(WATERLOGGED) && fluid.is(FluidTags.WATER) && fluid.defaultFluidState().isSource();
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
            BlockState blockstate = state.setValue(WATERLOGGED, Boolean.TRUE);
            if (state.getValue(LIT)) {
                SuperCandleWick.extinguish(null, level.getBlockState(pos.above()), level, pos.above());
                level.setBlock(pos, blockstate.setValue(LIT, false), 3);
            }
            else {
                level.setBlock(pos, blockstate, 3);
            }

            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && oldState.is(BzBlocks.POTION_BASE_CANDLE.get()) && state.is(BzBlocks.POTION_BASE_CANDLE.get())) {
            if (oldState.getValue(LIT) && !state.getValue(LIT)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity) {
                    potionCandleBlockEntity.resetCurrentDuration();
                    potionCandleBlockEntity.resetInstantStartTime();
                }
            }
            else if (!oldState.getValue(LIT) && state.getValue(LIT)) {
                resetTimingFields(level, pos);
            }
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (player.mayInteract(level, blockPos) && (!PlatformHooks.isNeoForge() || !PlatformHooks.isItemAbility(itemStack, null, "firestarter_light"))) {
            if (CandleUnlightBehaviors(itemStack, blockState, level, blockPos, player, false) || CandleLightBehaviors(itemStack, blockState, level, blockPos, player, interactionHand, false)) {
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (projectile.isOnFire() && SuperCandle.canBeLit(level, state, hit.getBlockPos())) {
            boolean litWick = SuperCandleWick.setLit(level, level.getBlockState(hit.getBlockPos().above()), hit.getBlockPos().above(), true);
            if (litWick && projectile.getOwner() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = level.getBlockEntity(hit.getBlockPos());
                if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity &&
                    potionCandleBlockEntity.getMobEffect() != null &&
                    potionCandleBlockEntity.getMobEffect().value().isInstantenous() &&
                    !potionCandleBlockEntity.getMobEffect().value().isBeneficial())
                {
                    BzCriterias.PROJECTILE_LIGHT_INSTANT_POTION_CANDLE_TRIGGER.get().trigger(serverPlayer);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player != null && player.getAbilities().instabuild) {
            return super.playerWillDestroy(level, pos, state, player);
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity) {
            ItemStack itemStack = BzItems.POTION_CANDLE.get().getDefaultInstance();
            potionCandleBlockEntity.saveToItem(itemStack, level.registryAccess());
            ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BzBlockEntities.POTION_CANDLE.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BzBlockEntities.POTION_CANDLE.get(), level.isClientSide ? (a, b, c, d) -> {} : PotionCandleBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.is(BzTags.CANDLES) && blockState.getValue(LIT) ? 5 : 0;
    }

    private void resetTimingFields(Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity) {
            potionCandleBlockEntity.resetCurrentDuration();
            potionCandleBlockEntity.setInstantStartTime(Math.max(level.getGameTime() - 1L, 0L));
        }
    }

    public static int getItemColor(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData != null && !customData.isEmpty()) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains(PotionCandleBlockEntity.COLOR_TAG)) {
                return tag.getInt(PotionCandleBlockEntity.COLOR_TAG);
            }
        }
        return PotionCandleBlockEntity.DEFAULT_COLOR;
    }

    public static int getBlockColor(BlockAndTintGetter world, BlockPos pos, int tintIndex) {
        if (world != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity) {
                int currentColor = potionCandleBlockEntity.getColor();

                if (tintIndex == 1) {
                    // Change tint of lit top
                    int red = Math.max((currentColor >> 16 & 255), 40);
                    int green = Math.max((currentColor >> 8 & 255), 10);
                    int blue = Math.max((currentColor & 255), 5);
                    currentColor = (Math.min(red + 60, 255) << 16) + (Math.min(green + 30, 255) << 8) + Math.min(blue + 25, 255);
                }
                return currentColor;
            }
        }
        return tintIndex;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos position, RandomSource random) {
        if (blockState.hasProperty(LIT) && blockState.getValue(LIT)) {
            BlockEntity blockEntity = level.getBlockEntity(position);
            if (blockEntity instanceof PotionCandleBlockEntity potionCandleBlockEntity && potionCandleBlockEntity.getMobEffect() != null) {
                int color = potionCandleBlockEntity.getColor();
                Vec3 colorRGB = PotionCandleBlockEntity.convertIntegerColorToRGB(color);

                //number of particles in this tick
                for (int i = 0; i < random.nextInt(3); ++i) {
                    this.spawnEffectParticles(level, random, position, (float) colorRGB.x(), (float) colorRGB.y(), (float) colorRGB.z());
                }
            }
        }
        super.animateTick(blockState, level, position, random);
    }


    private void spawnEffectParticles(Level level, RandomSource random, BlockPos position, float red, float green, float blue) {
        level.addParticle(
                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue),
                position.getX() + 0.4d + (random.nextDouble() * 0.2d),
                position.getY() + 0.7d + (random.nextDouble() * 0.2d),
                position.getZ() + 0.4d + (random.nextDouble() * 0.2d),
                0,
                0,
                0);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return !(blockState.hasProperty(LIT) && blockState.getValue(LIT));
    }

    @Override
    public PathType bz$getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) {
        if (state.hasProperty(LIT) && state.getValue(LIT)) {
            return PathType.DAMAGE_FIRE;
        }
        return null;
    }
}

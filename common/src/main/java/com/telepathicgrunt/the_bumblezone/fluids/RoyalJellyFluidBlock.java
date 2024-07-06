package com.telepathicgrunt.the_bumblezone.fluids;

import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.telepathicgrunt.the_bumblezone.fluids.base.BzLiquidBlock;
import com.telepathicgrunt.the_bumblezone.fluids.base.FluidGetter;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import static com.telepathicgrunt.the_bumblezone.fluids.HoneyFluidBlock.ABOVE_FLUID;
import static com.telepathicgrunt.the_bumblezone.fluids.HoneyFluidBlock.BOTTOM_LEVEL;
import static net.minecraft.world.level.material.FlowingFluid.FALLING;

public class RoyalJellyFluidBlock extends LiquidBlock implements FluidGetter {

    public static final int maxBottomLayer = 8;

    public RoyalJellyFluidBlock(FluidData baseFluid) {
        super(baseFluid.still().get(), BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_PURPLE)
                .liquid()
                .noCollission()
                .strength(100.0F, 100.0F)
                .speedFactor(0.15F)
                .noLootTable()
                .replaceable()
                .sound(SoundType.EMPTY)
                .pushReaction(PushReaction.DESTROY));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(BOTTOM_LEVEL, 0)
                .setValue(FALLING, false)
                .setValue(ABOVE_FLUID, false));

        baseFluid.setBlock(() -> this);
    }

    @Override
    public FlowingFluid getFluid() {
        return this.fluid;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(LEVEL, BOTTOM_LEVEL, FALLING, ABOVE_FLUID);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        if (this.neighboringFluidInteractions(world, blockPos)) {
            world.scheduleTick(blockPos, blockState.getFluidState().getType(), RoyalJellyFluid.adjustedFlowSpeed(this.getFluid().getTickDelay(world), world, blockPos));
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState previousBlockState, LevelAccessor level, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getFluidState().isSource() || previousBlockState.getFluidState().isSource()) {
            level.scheduleTick(blockPos, blockState.getFluidState().getType(), RoyalJellyFluid.adjustedFlowSpeed(this.getFluid().getTickDelay(level), level, blockPos));
        }
        return super.updateShape(blockState, direction, previousBlockState, level, blockPos, blockPos1);
    }

    @Override
    public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState previousBlockState, boolean notify) {
        if (this.neighboringFluidInteractions(world, blockPos)) {
            world.scheduleTick(blockPos, blockState.getFluidState().getType(), RoyalJellyFluid.adjustedFlowSpeed(this.getFluid().getTickDelay(world), world, blockPos));
        }
    }

    private boolean neighboringFluidInteractions(Level world, BlockPos pos)  {
        boolean lavaflag = false;
        boolean lavadownflag = false;

        for (Direction direction : Direction.values()) {
            BlockPos sidePos = pos.relative(direction);
            FluidState sideFluid = world.getFluidState(sidePos);
            if (sideFluid.is(FluidTags.LAVA)) {
                lavaflag = true;
                if (direction == Direction.DOWN) {
                    lavadownflag = true;
                }
                break;
            }
            else if(!sideFluid.isEmpty() && !sideFluid.is(BzTags.HONEY_FLUID)) {
                FluidState currentFluid = world.getFluidState(pos);
                if (direction == Direction.DOWN && currentFluid.hasProperty(BOTTOM_LEVEL) && currentFluid.getValue(BOTTOM_LEVEL) != 0) {
                    continue;
                }

                if (direction == Direction.UP) {
                    world.setBlock(pos, BzBlocks.GLISTERING_HONEY_CRYSTAL.get().defaultBlockState(), 3);
                    return false;
                }

                BlockState sideState = world.getBlockState(sidePos);
                if (sideState.getBlock() instanceof LiquidBlock || !sideState.getFluidState().isEmpty() || sideState.canBeReplaced()) {
                    world.setBlock(sidePos, BzBlocks.GLISTERING_HONEY_CRYSTAL.get().defaultBlockState(), 3);
                }
                else if (!currentFluid.isSource()) {
                    world.setBlock(pos, BzBlocks.GLISTERING_HONEY_CRYSTAL.get().defaultBlockState(), 3);
                    return false;
                }
            }
        }

        if (lavaflag) {
            FluidState currentFluid = world.getFluidState(pos);
            if (currentFluid.isSource()) {
                world.setBlockAndUpdate(pos, BzBlocks.SUGAR_INFUSED_STONE.get().defaultBlockState());
                this.triggerMixEffects(world, pos);
                return false;
            }

            if (!lavadownflag || currentFluid.hasProperty(BOTTOM_LEVEL) && currentFluid.getValue(BOTTOM_LEVEL) == 0) {
                world.setBlockAndUpdate(pos, BzBlocks.SUGAR_INFUSED_COBBLESTONE.get().defaultBlockState());
                this.triggerMixEffects(world, pos);
                return false;
            }
        }

        return true;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        int fluidLevel = Math.min(Math.max(blockState.getValue(LEVEL), 0), 8);
        int bottomFluidLevel = Math.min(Math.max(blockState.getValue(BOTTOM_LEVEL), 0), maxBottomLayer);
        boolean isFalling = blockState.getValue(FALLING);
        FluidState fluidState;
        if(fluidLevel == 0) {
            fluidState = this.getFluid().getSource(false);
        }
        else {
            fluidState = this.getFluid().getFlowing(fluidLevel, isFalling).setValue(BOTTOM_LEVEL, bottomFluidLevel);
        }
        return fluidState.setValue(ABOVE_FLUID, blockState.getValue(ABOVE_FLUID));
    }

    /**
     * Heal bees if they are damaged or create honey source if pollinated
     */
    @Override
    public void entityInside(BlockState state, Level world, BlockPos position, Entity entity) {
        double verticalSpeedDeltaLimit = 0.01D;
        if (entity instanceof Bee beeEntity && !beeEntity.isDeadOrDying()) {
            if (PlatformHooks.isEyesInNoFluid(entity)) {
                if (beeEntity.getHealth() < beeEntity.getMaxHealth()) {
                    float diff = beeEntity.getMaxHealth() - beeEntity.getHealth();
                    beeEntity.heal(diff);
                }

                beeEntity.addEffect(new MobEffectInstance(
                        BzEffects.BEENERGIZED.holder(),
                        600,
                        0,
                        false,
                        true,
                        true));
                beeEntity.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        600,
                        0,
                        false,
                        false,
                        true));
            }
            else {
                beeEntity.removeEffect(MobEffects.REGENERATION);
            }
        }
        else if(Math.abs(entity.getDeltaMovement().y()) > verticalSpeedDeltaLimit && entity.fallDistance <= 0.2D) {
            Vec3 vec3 = entity.getDeltaMovement();
            entity.setDeltaMovement(new Vec3(vec3.x(), Math.copySign(verticalSpeedDeltaLimit, vec3.y()), vec3.z()));
        }

        if (entity instanceof ServerPlayer serverPlayer && EssenceOfTheBees.hasEssence(serverPlayer)) {
            serverPlayer.addEffect(new MobEffectInstance(
                    BzEffects.BEENERGIZED.holder(),
                    300,
                    0,
                    false,
                    true,
                    true));
            serverPlayer.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    100,
                    1,
                    false,
                    false,
                    true));
        }

        super.entityInside(state, world, position, entity);
    }

    private void triggerMixEffects(Level world, BlockPos pos) {
        world.levelEvent(1501, pos, 0);
    }
}

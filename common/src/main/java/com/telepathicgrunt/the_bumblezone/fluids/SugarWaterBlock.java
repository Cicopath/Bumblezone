package com.telepathicgrunt.the_bumblezone.fluids;

import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.telepathicgrunt.the_bumblezone.fluids.base.BzLiquidBlock;
import com.telepathicgrunt.the_bumblezone.fluids.base.FluidGetter;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;


public class SugarWaterBlock extends LiquidBlock implements FluidGetter {

    public SugarWaterBlock(FluidData baseFluid) {
        super(baseFluid.still().get(), BlockBehaviour.Properties.of()
                .mapColor(MapColor.WATER)
                .liquid()
                .noCollission()
                .strength(100.0F, 100.0F)
                .speedFactor(0.95F)
                .noLootTable()
                .replaceable()
                .sound(SoundType.EMPTY)
                .pushReaction(PushReaction.DESTROY));
        baseFluid.setBlock(() -> this);
    }

    @Override
    public FlowingFluid getFluid() {
        return this.fluid;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (this.shouldSpreadLiquid(world, pos, state)) {
            world.scheduleTick(pos, state.getFluidState().getType(), this.getFluid().getTickDelay(world));
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor world, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getFluidState().isSource()) {
            SugarWaterBubbleColumnBlock.updateColumn(world, blockPos, world.getBlockState(blockPos.below()));
        }

        return super.updateShape(blockState, direction, blockState2, world, blockPos, blockPos2);
    }

    @Override
    public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState previousBlockState, boolean notify) {
        super.onPlace(blockState, world, blockPos, previousBlockState, notify);
        if (blockState.getFluidState().isSource()) {
            SugarWaterBubbleColumnBlock.updateColumn(world, blockPos, world.getBlockState(blockPos.below()));
        }
    }

    private boolean shouldSpreadLiquid(Level world, BlockPos pos, BlockState state)  {
        boolean flag = false;

        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && world.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                flag = true;
                break;
            }
        }

        if (flag) {
            FluidState ifluidstate = world.getFluidState(pos);
            if (ifluidstate.isSource()) {
                world.setBlockAndUpdate(pos, BzBlocks.SUGAR_INFUSED_STONE.get().defaultBlockState());
                this.triggerMixEffects(world, pos);
                return false;
            }

            if (ifluidstate.getHeight(world, pos) >= 0.44444445F) {
                world.setBlockAndUpdate(pos, BzBlocks.SUGAR_INFUSED_COBBLESTONE.get().defaultBlockState());
                this.triggerMixEffects(world, pos);
                return false;
            }
        }

        return true;
    }


    /**
     * Heal bees slightly if they are in Sugar Water and aren't taking damage.
     */
    @Deprecated
    @Override
    public void entityInside(BlockState state, Level world, BlockPos position, Entity entity) {
        if (entity instanceof Bee beeEntity && !beeEntity.isDeadOrDying()) {
            if (beeEntity.hurtMarked) {
                beeEntity.heal(1);
            }
        }

        super.entityInside(state, world, position, entity);
    }

    private void triggerMixEffects(Level world, BlockPos pos) {
        world.levelEvent(1501, pos, 0);
    }
}

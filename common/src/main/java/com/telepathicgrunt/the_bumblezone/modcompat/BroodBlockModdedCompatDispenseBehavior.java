package com.telepathicgrunt.the_bumblezone.modcompat;

import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.telepathicgrunt.the_bumblezone.blocks.EmptyHoneycombBrood;
import com.telepathicgrunt.the_bumblezone.blocks.HoneycombBrood;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.mixin.blocks.DefaultDispenseItemBehaviorInvoker;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;


public class BroodBlockModdedCompatDispenseBehavior extends DefaultDispenseItemBehavior {
    public static final DefaultDispenseItemBehavior DEFAULT_DROP_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior();

    private DispenseItemBehavior originalModdedDispenseItemBehavior;
    private Function6<DispenseItemBehavior, BlockSource, ItemStack, ServerLevel, BlockPos, BlockState, ItemStack> behaviorToRun;

    public BroodBlockModdedCompatDispenseBehavior(
            DispenseItemBehavior originalModdedDispenseItemBehavior,
            Function6<DispenseItemBehavior, BlockSource, ItemStack, ServerLevel, BlockPos, BlockState, ItemStack> behaviorToRun)
    {
        this.originalModdedDispenseItemBehavior = originalModdedDispenseItemBehavior;
        this.behaviorToRun = behaviorToRun;
    }

    /**
     * Dispense the specified stack, play the dispenser sound and spawn particles.
     */
    public ItemStack execute(BlockSource source, ItemStack stack) {
        ServerLevel world = source.getLevel();
        Position dispensePosition = DispenserBlock.getDispensePosition(source);
        BlockPos dispenseBlockPos = BlockPos.containing(dispensePosition);
        BlockState blockstate = world.getBlockState(dispenseBlockPos);

        if (blockstate.getBlock() == BzBlocks.EMPTY_HONEYCOMB_BROOD.get()) {
            return behaviorToRun.apply(originalModdedDispenseItemBehavior, source, stack, world, dispenseBlockPos, blockstate);
        }
        else {
            return ((DefaultDispenseItemBehaviorInvoker) originalModdedDispenseItemBehavior).invokeExecute(source, stack);
        }
    }



    /**
     * Play the dispenser sound from the specified block.
     */
    protected void playSound(BlockSource source) {
        source.getLevel().levelEvent(1002, source.getPos(), 0);
    }
}

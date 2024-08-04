package com.telepathicgrunt.the_bumblezone.blocks;

import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Optional;


public interface AncientWaxBase {

    default void applyEntityEffects(BlockState currentState, Entity collidingEntity) {
        if (collidingEntity instanceof LivingEntity livingEntity &&
            !collidingEntity.level().isClientSide() &&
            collidingEntity.tickCount % 10 == 2)
        {
            if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) {
                return;
            }

            if (livingEntity.getType().is(BzTags.ANCIENT_WAX_IMMUNE_TO_EFFECTS)) {
                return;
            }

            if (!(livingEntity instanceof ServerPlayer player && EssenceOfTheBees.hasEssence(player))) {
                livingEntity.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        95,
                        0,
                        true,
                        false,
                        true));

                livingEntity.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,
                        100,
                        2,
                        true,
                        false,
                        true));

                livingEntity.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        100,
                        2,
                        true,
                        false,
                        true));
            }
        }
    }

    default ItemInteractionResult swapBlocks(Level level, BlockState currentState, BlockPos blockPos, TagKey<Block> blockIterationTag) {
        Optional<HolderSet.Named<Block>> tagEntries = BuiltInRegistries.BLOCK.getTag(blockIterationTag);
        if (tagEntries.isPresent() && tagEntries.get().size() > 1) {

            List<Block> blockList = tagEntries.get().stream().map(Holder::value).toList();
            int indexOfCurrentBlock = blockList.indexOf(currentState.getBlock());
            if (indexOfCurrentBlock != -1) {
                Block newBlock = (indexOfCurrentBlock + 1 == blockList.size()) ?
                        blockList.get(0) : blockList.get(indexOfCurrentBlock + 1);

                BlockState newState = newBlock.defaultBlockState();
                for(Property<?> property : currentState.getProperties()) {
                    if(newState.hasProperty(property)) {
                        newState = GeneralUtils.getStateWithProperty(newState, currentState, property);
                    }
                }

                level.setBlock(blockPos, newState, 3);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

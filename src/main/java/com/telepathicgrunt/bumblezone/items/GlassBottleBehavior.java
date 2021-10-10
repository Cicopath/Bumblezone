package com.telepathicgrunt.bumblezone.items;

import com.telepathicgrunt.bumblezone.blocks.HoneyFluidBlock;
import com.telepathicgrunt.bumblezone.fluids.SugarWaterFluid;
import com.telepathicgrunt.bumblezone.modinit.BzItems;
import com.telepathicgrunt.bumblezone.tags.BzFluidTags;
import com.telepathicgrunt.bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class GlassBottleBehavior {

    public static boolean useBottleOnSugarWater(Level world, Player playerEntity, InteractionHand playerHand, BlockPos blockPos) {
        if (world.getFluidState(blockPos).getType() instanceof SugarWaterFluid) {
            ItemStack itemstack = playerEntity.getItemInHand(playerHand);

            if (itemstack.getItem() == Items.GLASS_BOTTLE) {
                world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);

                if(!playerEntity.isCreative()) {
                    itemstack.shrink(1);
                    GeneralUtils.givePlayerItem(playerEntity, playerHand, new ItemStack(BzItems.SUGAR_WATER_BOTTLE), false);
                }
                return true;
            }
        }

        return false;
    }


    public static boolean useBottleOnHoneyFluid(Level world, Player playerEntity, InteractionHand playerHand, BlockPos blockPos) {
        FluidState currentFluidState = world.getFluidState(blockPos);
        if (currentFluidState.is(BzFluidTags.BZ_HONEY_FLUID) && currentFluidState.isSource()) {
            ItemStack itemstack = playerEntity.getItemInHand(playerHand);
            world.setBlock(blockPos, currentFluidState.createLegacyBlock().setValue(HoneyFluidBlock.LEVEL, 5), 3); // reduce honey
            world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);

            if(!playerEntity.isCreative()) {
                itemstack.shrink(1);
                GeneralUtils.givePlayerItem(playerEntity, playerHand, new ItemStack(Items.HONEY_BOTTLE), false);
            }

//            if ((playerEntity.getCommandSenderWorld().dimension().location().equals(Bumblezone.MOD_DIMENSION_ID) ||
//                    Bumblezone.BzBeeAggressionConfig.allowWrathOfTheHiveOutsideBumblezone.get()) &&
//                    !playerEntity.isCreative() &&
//                    !playerEntity.isSpectator() &&
//                    Bumblezone.BzBeeAggressionConfig.aggressiveBees.get())
//            {
//                if(playerEntity.hasEffect(BzEffects.PROTECTION_OF_THE_HIVE.get())){
//                    playerEntity.removeEffect(BzEffects.PROTECTION_OF_THE_HIVE.get());
//                }
//                else{
//                    //Now all bees nearby in Bumblezone will get VERY angry!!!
//                    playerEntity.addEffect(new EffectInstance(BzEffects.WRATH_OF_THE_HIVE.get(), Bumblezone.BzBeeAggressionConfig.howLongWrathOfTheHiveLasts.get(), 2, false, Bumblezone.BzBeeAggressionConfig.showWrathOfTheHiveParticles.get(), true));
//                }
//            }

            return true;
        }

        return false;
    }
}

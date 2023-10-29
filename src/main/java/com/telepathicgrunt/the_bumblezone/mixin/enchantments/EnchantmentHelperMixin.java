package com.telepathicgrunt.the_bumblezone.mixin.enchantments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.telepathicgrunt.the_bumblezone.items.BeeCannon;
import com.telepathicgrunt.the_bumblezone.items.CarpenterBeeBoots;
import com.telepathicgrunt.the_bumblezone.items.CrystalCannon;
import com.telepathicgrunt.the_bumblezone.items.HoneyCrystalShield;
import com.telepathicgrunt.the_bumblezone.items.StingerSpearItem;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(value = EnchantmentHelper.class, priority = 1020)
public class EnchantmentHelperMixin {

    //most compat way to make enchantment table not apply our enchantment
    @WrapOperation(method = "getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isDiscoverable()Z"))
    private static boolean thebumblezone_preventApplyingCertainEnchantmentsOnCertainItems(Enchantment enchantment,
                                                                 Operation<Boolean> originalDiscoverability,
                                                                 int power,
                                                                 ItemStack stack)
    {
        if(enchantment == BzEnchantments.COMB_CUTTER && !BzEnchantments.COMB_CUTTER.canEnchant(stack)) {
            return false;
        }
        else if(enchantment == BzEnchantments.NEUROTOXINS && !BzEnchantments.NEUROTOXINS.canEnchant(stack)) {
            return false;
        }
        else if(enchantment == BzEnchantments.POTENT_POISON && !BzEnchantments.POTENT_POISON.canEnchant(stack)) {
            return false;
        }
        else if(HoneyCrystalShield.isInvalidForHoneyCrystalShield(stack, enchantment)) {
            return false;
        }
        else if(StingerSpearItem.isInvalidForStingerSpear(stack, enchantment)) {
            return false;
        }

        return originalDiscoverability.call(enchantment);
    }

    // Apply enchantments that normally would not be able to be applied
    @Inject(method = "getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isTreasureOnly()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void thebumblezone_applyEnchantmentsForUnusualItems(int power, ItemStack stack, boolean treasureAllowed,
                                                                 CallbackInfoReturnable<List<EnchantmentInstance>> cir,
                                                                 List<EnchantmentInstance> list, Item item,
                                                                 boolean treasure, Iterator<Enchantment> var6, Enchantment enchantment)
    {
        if (CarpenterBeeBoots.canBeEnchanted(stack, enchantment) ||
            BeeCannon.canBeEnchanted(stack, enchantment) ||
            CrystalCannon.canBeEnchanted(stack, enchantment) ||
            StingerSpearItem.canBeEnchanted(stack, enchantment)
        ) {
            if ((!enchantment.isTreasureOnly() || treasure) && enchantment.isDiscoverable()) {
                for(int currentLevel = enchantment.getMaxLevel(); currentLevel > enchantment.getMinLevel() - 1; --currentLevel) {
                    if (power >= enchantment.getMinCost(currentLevel) && power <= enchantment.getMaxCost(currentLevel)) {
                        list.add(new EnchantmentInstance(enchantment, currentLevel));
                        break;
                    }
                }
            }
        }
    }
}
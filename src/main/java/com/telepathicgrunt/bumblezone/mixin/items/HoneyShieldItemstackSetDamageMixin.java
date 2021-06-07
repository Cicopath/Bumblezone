package com.telepathicgrunt.bumblezone.mixin.items;

import com.telepathicgrunt.bumblezone.items.HoneyCrystalShieldBehavior;
import com.telepathicgrunt.bumblezone.modinit.BzItems;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class HoneyShieldItemstackSetDamageMixin {

    @Inject(method = "setDamage",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void thebumblezone_isHoneyCrystalShield(int damage, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if(stack.getItem() == BzItems.HONEY_CRYSTAL_SHIELD) {
            stack.getOrCreateTag().putInt("Damage", Math.max(0, HoneyCrystalShieldBehavior.setDamage(stack, damage)));
            ci.cancel();
        }
    }
}
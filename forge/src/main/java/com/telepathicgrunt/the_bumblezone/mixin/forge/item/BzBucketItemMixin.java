package com.telepathicgrunt.the_bumblezone.mixin.forge.item;

import com.telepathicgrunt.the_bumblezone.fluids.base.BzBucketItem;
import com.telepathicgrunt.the_bumblezone.fluids.base.FluidGetter;
import com.telepathicgrunt.the_bumblezone.fluids.base.FluidInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(BzBucketItem.class)
public class BzBucketItemMixin extends BucketItem implements FluidGetter {

    @Unique
    private Supplier<? extends FlowingFluid> bz$fluidSupplier;

    public BzBucketItemMixin(Fluid arg, Properties arg2) {
        super(arg, arg2);
    }

    @Inject(method = "<init>(Lcom/telepathicgrunt/the_bumblezone/fluids/base/FluidInfo;Lnet/minecraft/world/item/Item$Properties;)V", at = @At("RETURN"))
    public void bumblezone$onInit(FluidInfo info, Properties properties, CallbackInfo ci) {
        this.bz$fluidSupplier = info::source;
    }

    @NotNull
    @Override
    public FlowingFluid getFluid() {
        return bz$fluidSupplier.get();
    }

    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}

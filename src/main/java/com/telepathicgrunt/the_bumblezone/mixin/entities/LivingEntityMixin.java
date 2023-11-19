package com.telepathicgrunt.the_bumblezone.mixin.entities;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.telepathicgrunt.the_bumblezone.components.MiscComponent;
import com.telepathicgrunt.the_bumblezone.effects.HiddenEffect;
import com.telepathicgrunt.the_bumblezone.effects.ParalyzedEffect;
import com.telepathicgrunt.the_bumblezone.effects.WrathOfTheHiveEffect;
import com.telepathicgrunt.the_bumblezone.enchantments.NeurotoxinsEnchantment;
import com.telepathicgrunt.the_bumblezone.entities.BeeAggression;
import com.telepathicgrunt.the_bumblezone.entities.EntityTeleportationHookup;
import com.telepathicgrunt.the_bumblezone.fluids.HoneyFluid;
import com.telepathicgrunt.the_bumblezone.items.HoneyBeeLeggings;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    protected ItemStack useItem;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "isImmobile()Z",
            at = @At(value = "RETURN"))
    private boolean thebumblezone_isParalyzedCheck(boolean isImmobile) {
        if(!isImmobile && ParalyzedEffect.isParalyzed((LivingEntity)(Object)this)) {
            return true;
        }
        return isImmobile;
    }

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "HEAD"))
    private void thebumblezone_entityHurt(DamageSource source, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        NeurotoxinsEnchantment.entityHurt(this, source);
    }

    //bees become angrier when hit in bumblezone
    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At(value = "HEAD"))
    private void thebumblezone_onEntityDamaged(DamageSource source, float amount, CallbackInfo ci) {
        //Bumblezone.LOGGER.log(Level.INFO, "started");
        BeeAggression.beeHitAndAngered(((LivingEntity)(Object)this), source.getEntity());
    }

    //clear the wrath effect from all bees if they killed their target
    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At(value = "HEAD"))
    private void thebumblezone_onDeath(DamageSource source, CallbackInfo ci) {
        WrathOfTheHiveEffect.calmTheBees(this.level, (LivingEntity)(Object)this);
    }

    // Handles teleportation and armor stand ticking
    @Inject(method = "tick",
            at = @At(value = "TAIL"))
    private void thebumblezone_onLivingEntityTick(CallbackInfo ci) {
        EntityTeleportationHookup.entityTick((LivingEntity) (Object) this);
        HoneyBeeLeggings.armorStandTick((LivingEntity) (Object) this);
    }

    //hides entities with hidden effect
    @ModifyReturnValue(method = "getVisibilityPercent(Lnet/minecraft/world/entity/Entity;)D",
            at = @At(value = "RETURN"))
    private double thebumblezone_hideEntityWithHiddenEffect(double currentVisibility, Entity entity) {
        double newVisibility = HiddenEffect.hideEntity(entity, currentVisibility);
        if(currentVisibility != newVisibility) {
            return newVisibility;
        }
        return currentVisibility;
    }

    @Inject(method = "completeUsingItem()V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private void thebumblezone_onHoneyBottleFinish(CallbackInfo ci) {
        MiscComponent.onHoneyBottleDrank((LivingEntity)(Object)this, useItem);
    }

    //-----------------------------------------------------------//

    //deplete air supply
    @Inject(method = "baseTick()V",
            at = @At(value = "TAIL"))
    private void thebumblezone_breathing(CallbackInfo ci) {
        HoneyFluid.breathing((LivingEntity)(Object)this);
    }

    // make jumping in honey and sugar water weaker
    @WrapOperation(method = "aiStep()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D", ordinal = 1),
            require = 0)
    private double thebumblezone_customFluidJumpWeaker(LivingEntity livingEntity, TagKey<Fluid> tagKey, Operation<Double> original) {
        double newFluidHeight = this.getFluidHeight(BzTags.SPECIAL_HONEY_LIKE);
        if(newFluidHeight > 0) {
            return newFluidHeight;
        }
        newFluidHeight = this.getFluidHeight(BzTags.SUGAR_WATER_FLUID);
        if(newFluidHeight > 0) {
            return newFluidHeight;
        }
        return original.call(livingEntity, tagKey);
    }
}
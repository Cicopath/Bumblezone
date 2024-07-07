package com.telepathicgrunt.the_bumblezone.mixin.fabric.entity;

import com.telepathicgrunt.the_bumblezone.events.entity.BzEntityTravelingToDimensionEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {


    @Inject(method = "changeDimension",
            at = @At("HEAD"),
            cancellable = true)
    private void bumblezone$onTravelToDimension(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
        if (BzEntityTravelingToDimensionEvent.EVENT.invoke(new BzEntityTravelingToDimensionEvent(dimensionTransition.newLevel().dimension(), (ServerPlayer)(Object)this))) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;level()Lnet/minecraft/world/level/Level;"),
            cancellable = true)
    private void bumblezone$onTeleportTo(ServerLevel serverLevel, double d, double e, double f, float g, float h, CallbackInfo ci) {
        if (BzEntityTravelingToDimensionEvent.EVENT.invoke(new BzEntityTravelingToDimensionEvent(serverLevel.dimension(), (ServerPlayer)(Object)this))) {
            ci.cancel();
        }
    }
}

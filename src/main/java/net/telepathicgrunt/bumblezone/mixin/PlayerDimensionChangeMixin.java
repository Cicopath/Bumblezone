package net.telepathicgrunt.bumblezone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.telepathicgrunt.bumblezone.entities.PlayerTeleportation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class PlayerDimensionChangeMixin {
    // Handles storing of past non-bumblezone dimension the player is leaving
    @Inject(method = "moveToWorld",
            at = @At(value = "HEAD"))
    private void onDimensionChange(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        PlayerTeleportation.playerLeavingBz(destination.getRegistryKey().getValue(), ((ServerPlayerEntity)(Object)this));
    }
}
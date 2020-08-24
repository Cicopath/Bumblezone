package net.telepathicgrunt.bumblezone.mixin;

import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.HitResult;
import net.telepathicgrunt.bumblezone.entities.PlayerTeleportation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public class EnderpearlImpactMixin {

    // Teleports player to Bumblezone when pearl hits bee nest
    @Inject(method = "onCollision",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void onPearlHit(HitResult hitResult, CallbackInfo ci) {
        if(PlayerTeleportation.runEnderpearlImpact(hitResult, ((EnderPearlEntity) (Object) this))) ci.cancel();
    }
}
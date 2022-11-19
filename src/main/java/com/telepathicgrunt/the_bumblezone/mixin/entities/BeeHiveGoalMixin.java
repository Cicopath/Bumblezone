package com.telepathicgrunt.the_bumblezone.mixin.entities;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bee.BeeGoToHiveGoal.class)
public class BeeHiveGoalMixin {

    @Final
    @Shadow(aliases = "field_20371")
    private Bee field_20371;

    /**
     * @author TelepathicGrunt
     * @reason Always use the entity's own randomSource instead of world's when creating/initing entities or else you risk a crash from threaded worldgen entity spawning. Fixed this bug with vanilla bees.
     */
    @ModifyReceiver(method = "<init>(Lnet/minecraft/world/entity/animal/Bee;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private RandomSource thebumblezone_fixGoalRandomSourceUsage1(RandomSource randomSource, int range) {
        return field_20371.getRandom();
    }
}
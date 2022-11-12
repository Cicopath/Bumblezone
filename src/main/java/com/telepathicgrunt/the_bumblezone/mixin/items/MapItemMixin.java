package com.telepathicgrunt.the_bumblezone.mixin.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MapItem.class)
public class MapItemMixin {
    @ModifyExpressionValue(method = "update(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z", ordinal = 1),
            require = 0)
    private boolean thebumblezone_filledMapForDimension1(boolean ceiling, Level level) {
        if (level.dimension().equals(BzDimension.BZ_WORLD_KEY)) {
            return false;
        }
        return ceiling;
    }

    @ModifyVariable(method = "update(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getMinBuildHeight()I", shift = At.Shift.BEFORE, ordinal = 0),
            ordinal = 17,
            require = 0)
    private int thebumblezone_filledMapForDimension2(int scanHeight, Level level) {
        if (level.dimension().equals(BzDimension.BZ_WORLD_KEY) && scanHeight >= 250) {
            return 110;
        }
        return scanHeight;
    }
}
package com.telepathicgrunt.the_bumblezone.events.client;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public record BzRegisterDimensionEffectsEvent(BiConsumer<ResourceLocation, DimensionSpecialEffects> effects) {

    public static final EventHandler<BzRegisterDimensionEffectsEvent> EVENT = new EventHandler<>();

    public void register(ResourceLocation dimension, DimensionSpecialEffects effect) {
        effects.accept(dimension, effect);
    }
}

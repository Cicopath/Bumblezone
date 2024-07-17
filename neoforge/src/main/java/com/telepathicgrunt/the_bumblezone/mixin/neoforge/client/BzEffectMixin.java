package com.telepathicgrunt.the_bumblezone.mixin.neoforge.client;

import com.telepathicgrunt.the_bumblezone.client.rendering.MobEffectRenderer;
import com.telepathicgrunt.the_bumblezone.effects.BzEffect;
import com.telepathicgrunt.the_bumblezone.utils.LazySupplier;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(BzEffect.class)
public class BzEffectMixin extends MobEffect {

    protected BzEffectMixin(MobEffectCategory arg, int i) {
        super(arg, i);
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        final LazySupplier<MobEffectRenderer> renderer = LazySupplier.of(() -> MobEffectRenderer.RENDERERS.get(BuiltInRegistries.MOB_EFFECT.getHolder(BuiltInRegistries.MOB_EFFECT.getKey(this)).get()));
        consumer.accept(new IClientMobEffectExtensions() {

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                return renderer.getOptional().map(r -> r.renderGuiIcon(instance, gui, guiGraphics, x, y, z, alpha)).orElse(false);
            }
        });
    }
}

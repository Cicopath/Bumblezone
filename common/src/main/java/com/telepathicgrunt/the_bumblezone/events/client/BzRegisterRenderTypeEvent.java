package com.telepathicgrunt.the_bumblezone.events.client;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiConsumer;

public record BzRegisterRenderTypeEvent(BiConsumer<Fluid, RenderType> fluid, BiConsumer<Block, RenderType> block) {

    public static final EventHandler<BzRegisterRenderTypeEvent> EVENT = new EventHandler<>();

    public void register(RenderType type, Fluid... fluids) {
        for (Fluid fluid : fluids) {
            this.fluid.accept(fluid, type);
        }
    }

    public void register(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            this.block.accept(block, type);
        }
    }

}

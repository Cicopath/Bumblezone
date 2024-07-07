package com.telepathicgrunt.the_bumblezone.events.client;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

public record BzRegisterBlockEntityRendererEvent<T extends BlockEntity>(BiConsumer<BlockEntityType<? extends T>, BlockEntityRendererProvider<T>> renderers) {

    public static final EventHandler<BzRegisterBlockEntityRendererEvent<?>> EVENT = new EventHandler<>();

    public void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
        renderers.accept(blockEntityType, blockEntityRendererProvider);
    }
}


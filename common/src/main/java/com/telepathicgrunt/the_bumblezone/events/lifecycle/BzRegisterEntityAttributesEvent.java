package com.telepathicgrunt.the_bumblezone.events.lifecycle;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;

public record BzRegisterEntityAttributesEvent(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> attributes) {

    public static final EventHandler<BzRegisterEntityAttributesEvent> EVENT = new EventHandler<>();

    public void register(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder builder) {
        attributes.accept(entityType, builder);
    }

}

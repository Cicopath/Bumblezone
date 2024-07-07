package com.telepathicgrunt.the_bumblezone.events.client;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public record BzRegisterKeyMappingEvent(Consumer<KeyMapping> mapper) {

    public static final EventHandler<BzRegisterKeyMappingEvent> EVENT = new EventHandler<>();

    public void register(KeyMapping... keyMappings) {
        for (KeyMapping keyMapping : keyMappings) {
            this.mapper.accept(keyMapping);
        }
    }
}

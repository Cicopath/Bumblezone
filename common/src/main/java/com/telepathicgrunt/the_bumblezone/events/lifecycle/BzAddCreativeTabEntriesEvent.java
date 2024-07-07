package com.telepathicgrunt.the_bumblezone.events.lifecycle;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public record BzAddCreativeTabEntriesEvent(Type type, CreativeModeTab tab, boolean hasPermission, Consumer<ItemStack> adder) {

    public static final EventHandler<BzAddCreativeTabEntriesEvent> EVENT = new EventHandler<>();

    public void add(ItemStack stack) {
        adder.accept(stack);
    }

    public void add(ItemLike item) {
        adder.accept(new ItemStack(item));
    }

    public enum Type {
        BUILDING,
        COLORED,
        NATURAL,
        FUNCTIONAL,
        REDSTONE,
        TOOLS,
        COMBAT,
        FOOD,
        INGREDIENTS,
        SPAWN_EGGS,
        OPERATOR,
        CUSTOM
    }
}

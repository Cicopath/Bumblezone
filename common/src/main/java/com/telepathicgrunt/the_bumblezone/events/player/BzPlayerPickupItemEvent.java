package com.telepathicgrunt.the_bumblezone.events.player;

import com.telepathicgrunt.the_bumblezone.events.base.EventHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record BzPlayerPickupItemEvent(Player player, ItemStack item) {

    public static final EventHandler<BzPlayerPickupItemEvent> EVENT = new EventHandler<>();
}

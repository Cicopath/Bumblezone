package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.entities.nonliving.HoneyCrystalShardEntity;
import com.telepathicgrunt.the_bumblezone.platform.BzArrowItem;
import com.telepathicgrunt.the_bumblezone.utils.OptionalBoolean;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class HoneyCrystalShards extends BzArrowItem {
    public HoneyCrystalShards(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity livingEntity) {
        return new HoneyCrystalShardEntity(level, livingEntity);
    }

    @Override
    public OptionalBoolean bz$isInfinite(ItemStack stack, ItemStack bow, Player player) {
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow);
        return enchantLevel > 0 ? OptionalBoolean.of(true) : OptionalBoolean.EMPTY;
    }
}

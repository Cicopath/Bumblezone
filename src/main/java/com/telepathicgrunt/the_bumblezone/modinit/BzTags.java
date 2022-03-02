package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.material.Fluid;

public class BzTags {
    // Sole purpose is to initalize the tag wrappers at mod startup
    public static void initTags() {}

    public static final TagKey<Block> REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "required_blocks_under_hive_to_teleport"));
    public static final TagKey<Block> BLACKLISTED_TELEPORTATION_HIVES = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "blacklisted_teleportable_hive_blocks"));
    public static final TagKey<Block> HONEYCOMBS_THAT_FEATURES_CAN_CARVE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "honeycombs_that_features_can_carve"));
    public static final TagKey<Block> WRATH_ACTIVATING_BLOCKS_WHEN_MINED = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "wrath_activating_blocks_when_mined"));
    public static final TagKey<Block> FLOWERS_ALLOWED_BY_POLLEN_PUFF = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "flowers_allowed_by_pollen_puff"));
    public static final TagKey<Block> FLOWERS_BLACKLISTED_FROM_POLLEN_PUFF = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Bumblezone.MODID, "flowers_blacklisted_from_pollen_puff"));

    public static final TagKey<Item> TURN_SLIME_TO_HONEY_SLIME = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "turn_slime_to_honey_slime"));
    public static final TagKey<Item> HONEY_CRYSTAL_SHIELD_REPAIR_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "honey_crystal_shield_repair_items"));
    public static final TagKey<Item> STINGER_SPEAR_REPAIR_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "stinger_spear_repair_items"));
    public static final TagKey<Item> BEE_ARMOR_REPAIR_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "bee_armor_repair_items"));
    public static final TagKey<Item> BEE_FEEDING_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "bee_feeding_items"));
    public static final TagKey<Item> WRATH_ACTIVATING_ITEMS_WHEN_PICKED_UP = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Bumblezone.MODID, "wrath_activating_items_when_picked_up"));
    public static final TagKey<Item> HONEY_BUCKETS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c:buckets/honey"));

    public static final TagKey<Fluid> HONEY_FLUID = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation("c", "honey"));
    public static final TagKey<Fluid> BZ_HONEY_FLUID = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(Bumblezone.MODID, "honey"));
    public static final TagKey<Fluid> VISUAL_HONEY_FLUID = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation("c", "visual/honey"));
    public static final TagKey<Fluid> VISUAL_WATER_FLUID = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation("c", "visual/water"));
    public static final TagKey<Fluid> CONVERTIBLE_TO_SUGAR_WATER = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(Bumblezone.MODID, "convertible_to_sugar_water"));

    public static final TagKey<EntityType<?>> POLLEN_PUFF_CAN_POLLINATE = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(Bumblezone.MODID, "pollen_puff_can_pollinate"));

    public static final TagKey<ConfiguredStructureFeature<?,?>> NO_DUNGEONS = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(Bumblezone.MODID, "no_dungeons"));
    public static final TagKey<ConfiguredStructureFeature<?,?>> WRATH_CAUSING = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(Bumblezone.MODID, "wrath_causing"));
}

package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class BzTags {
    // Sole purpose is to initalize the tag wrappers at mod startup
    public static void initTags() {}

    public static final TagKey<Block> REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "dimension_teleportation/required_blocks_under_beehive_to_teleport"));
    public static final TagKey<Block> BLACKLISTED_TELEPORTATION_HIVES = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "dimension_teleportation/blacklisted_teleportable_beehive_blocks"));
    public static final TagKey<Block> HONEYCOMBS_THAT_FEATURES_CAN_CARVE = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "worldgen_checks/honeycombs_that_features_can_carve"));
    public static final TagKey<Block> WRATH_ACTIVATING_BLOCKS_WHEN_MINED = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "bee_aggression_in_dimension/wrath_activating_blocks_when_mined"));
    public static final TagKey<Block> FLOWERS_ALLOWED_BY_POLLEN_PUFF = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "pollen_puff/multiplying_allowed_flowers"));
    public static final TagKey<Block> FLOWERS_BLACKLISTED_FROM_POLLEN_PUFF = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "pollen_puff/multiplying_disallowed_flowers"));
    public static final TagKey<Block> CARPENTER_BEE_BOOTS_MINEABLES = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "carpenter_bee_boots/mineables"));
    public static final TagKey<Block> CARPENTER_BEE_BOOTS_CLIMBABLES = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "carpenter_bee_boots/climbables"));
    public static final TagKey<Block> BLACKLISTED_HONEY_COMPASS_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "honey_compass/beehives_blacklisted_from_position_tracking"));
    public static final TagKey<Block> CANDLES = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "candles"));
    public static final TagKey<Block> CANDLE_WICKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "candle_wicks"));
    public static final TagKey<Block> CANDLE_BASES = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "candle_bases"));
    public static final TagKey<Block> CAVE_EDGE_BLOCKS_FOR_MODDED_COMPATS = TagKey.create(Registries.BLOCK, new ResourceLocation(Bumblezone.MODID, "worldgen_checks/cave_edge_blocks_for_modded_compats"));

    public static final TagKey<Item> TURN_SLIME_TO_HONEY_SLIME = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "turn_slime_to_honey_slime"));
    public static final TagKey<Item> HONEY_CRYSTAL_SHIELD_REPAIR_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "repair_items/honey_crystal_shield"));
    public static final TagKey<Item> STINGER_SPEAR_REPAIR_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "repair_items/stinger_spear"));
    public static final TagKey<Item> BEE_ARMOR_REPAIR_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "repair_items/bee_armor"));
    public static final TagKey<Item> BEE_CANNON_REPAIR_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "repair_items/bee_cannon"));
    public static final TagKey<Item> CRYSTAL_CANNON_REPAIR_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "repair_items/crystal_cannon"));
    public static final TagKey<Item> BEE_FEEDING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "bee_feedable_items"));
    public static final TagKey<Item> WRATH_ACTIVATING_ITEMS_WHEN_PICKED_UP = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "bee_aggression_in_dimension/wrath_activating_items_when_picked_up"));
    public static final TagKey<Item> HONEY_BUCKETS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "buckets/honey"));
    public static final TagKey<Item> ROYAL_JELLY_BUCKETS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "buckets/royal_jelly"));
    public static final TagKey<Item> SHULKER_BOXES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "shulker_boxes"));
    public static final TagKey<Item> SUPER_CANDLES_ITEM = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "super_candles"));
    public static final TagKey<Item> DAMAGEABLE_CANDLE_LIGHTING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "candle_lightables/damageable"));
    public static final TagKey<Item> CONSUMABLE_CANDLE_LIGHTING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "candle_lightables/consumable"));
    public static final TagKey<Item> INFINITE_CANDLE_LIGHTING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "candle_lightables/infinite"));
    public static final TagKey<Item> XP_2_WHEN_CONSUMED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/xp_2_when_consumed"));
    public static final TagKey<Item> XP_5_WHEN_CONSUMED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/xp_5_when_consumed"));
    public static final TagKey<Item> XP_25_WHEN_CONSUMED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/xp_25_when_consumed"));
    public static final TagKey<Item> XP_100_WHEN_CONSUMED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/xp_100_when_consumed"));
    public static final TagKey<Item> CANNOT_CONSUMED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/cannot_consume"));
    public static final TagKey<Item> CAN_BE_ENCHANTED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "crystalline_flower/can_be_enchanted"));
    public static final TagKey<Item> ENDERPEARL_TARGET_ARMOR = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "enderpearl_teleporting/target_armor"));
    public static final TagKey<Item> ENDERPEARL_TARGET_HELD_ITEM = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "enderpearl_teleporting/target_held_item"));
    public static final TagKey<Item> BEEHEMOTH_DESIRED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "mob_luring/beehemoth"));
    public static final TagKey<Item> BEEHEMOTH_FAST_LURING_DESIRED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "mob_luring/beehemoth_fast_luring"));
    public static final TagKey<Item> HONEY_SLIME_DESIRED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Bumblezone.MODID, "mob_luring/honey_slime"));

    public static final TagKey<Fluid> HONEY_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation("c", "honey"));
    public static final TagKey<Fluid> BZ_HONEY_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation(Bumblezone.MODID, "honey"));
    public static final TagKey<Fluid> VISUAL_HONEY_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation("c", "visual/honey"));
    public static final TagKey<Fluid> VISUAL_WATER_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation("c", "visual/water"));
    public static final TagKey<Fluid> CONVERTIBLE_TO_SUGAR_WATER = TagKey.create(Registries.FLUID, new ResourceLocation(Bumblezone.MODID, "convertible_to_sugar_water"));
    public static final TagKey<Fluid> ROYAL_JELLY_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation(Bumblezone.MODID, "royal_jelly"));
    public static final TagKey<Fluid> SUGAR_WATER_FLUID = TagKey.create(Registries.FLUID, new ResourceLocation(Bumblezone.MODID, "sugar_water"));
    public static final TagKey<Fluid> SPECIAL_HONEY_LIKE = TagKey.create(Registries.FLUID, new ResourceLocation(Bumblezone.MODID, "special_honey_like"));

    public static final TagKey<EntityType<?>> POLLEN_PUFF_CAN_POLLINATE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "pollen_puff/can_pollinate"));
    public static final TagKey<EntityType<?>> BLACKLISTED_BEE_CANNON_BEES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "bee_cannon/blacklisted_bees"));
    public static final TagKey<EntityType<?>> BLACKLISTED_STINGLESS_BEE_HELMET_PASSENGERS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "stingless_bee_helmet/blacklisted_passengers"));
    public static final TagKey<EntityType<?>> ENDERPEARL_TARGET_ENTITY_HIT_ANYWHERE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "enderpearl_teleporting/target_entity_hit_anywhere"));
    public static final TagKey<EntityType<?>> ENDERPEARL_TARGET_ENTITY_HIT_HIGH = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "enderpearl_teleporting/target_entity_hit_high"));
    public static final TagKey<EntityType<?>> ENDERPEARL_TARGET_ENTITY_HIT_LOW = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "enderpearl_teleporting/target_entity_hit_low"));
    public static final TagKey<EntityType<?>> FORCED_BEE_ANGRY_AT = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "bee_aggression_in_dimension/always_angry_at"));
    public static final TagKey<EntityType<?>> FORCED_BEE_CALM_AT = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "bee_aggression_in_dimension/forced_calm_at"));
    public static final TagKey<EntityType<?>> HANGING_GARDENS_INITIAL_SPAWN_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "hanging_garden/initial_spawn_entities"));

    public static final TagKey<Structure> NO_DUNGEONS = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "no_dungeons"));
    public static final TagKey<Structure> NO_CAVES = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "no_caves"));
    public static final TagKey<Structure> NO_HONEYCOMB_HOLES = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "no_honeycomb_holes"));
    public static final TagKey<Structure> NO_HONEYCOMB_HOLES_PIECEWISE = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "no_honeycomb_holes_piecewise"));
    public static final TagKey<Structure> WRATH_CAUSING = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "wrath_causing"));
    public static final TagKey<Structure> HONEY_COMPASS_DEFAULT_LOCATING = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "honey_compass/default_locating"));
    public static final TagKey<Structure> HONEY_COMPASS_THRONE_LOCATING = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "honey_compass/throne_locating"));
    public static final TagKey<Structure> BEE_QUEEN_MINING_FATIGUE = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Bumblezone.MODID, "bee_queen_mining_fatigue"));

    public static final TagKey<MobEffect> BLACKLISTED_INCENSE_CANDLE_EFFECTS = TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(Bumblezone.MODID, "blacklisted_incense_candle_effects"));

    public static final TagKey<Enchantment> BLACKLISTED_CRYSTALLINE_FLOWER_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Bumblezone.MODID, "blacklisted_crystalline_flower_enchantments"));
}

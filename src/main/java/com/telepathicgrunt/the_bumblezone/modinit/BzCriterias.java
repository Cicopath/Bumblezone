package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.advancements.EntitySpecificTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.GenericTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.ItemSpecificTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.RecipeDiscoveredTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class BzCriterias {
    // CRITERIA TRIGGERS
    public static final GenericTrigger BEENERGIZED_MAXED_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "beenergized_maxed"));
    public static final ItemSpecificTrigger BEE_DROP_POLLEN_PUFF_TRIGGER = new ItemSpecificTrigger(new ResourceLocation(Bumblezone.MODID, "bee_drop_pollen_puff"));
    public static final GenericTrigger BUMBLE_BEE_CHESTPLATE_MAX_FLIGHT_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bumble_bee_chestplate_max_flight"));
    public static final GenericTrigger CLEANUP_HONEY_WEB_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "cleanup_honey_web"));
    public static final GenericTrigger CLEANUP_STICKY_HONEY_RESIDUE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "cleanup_sticky_honey_residue"));
    public static final GenericTrigger COMB_CUTTER_EXTRA_DROPS_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "comb_cutter_extra_drops"));
    public static final EntitySpecificTrigger EXTENDED_WRATH_OF_THE_HIVE_TRIGGER = new EntitySpecificTrigger(new ResourceLocation(Bumblezone.MODID, "extended_wrath_of_the_hive"));
    public static final GenericTrigger FALLING_ON_POLLEN_BLOCK_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "fall_onto_pollen_pile"));
    public static final ItemSpecificTrigger FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER = new ItemSpecificTrigger(new ResourceLocation(Bumblezone.MODID, "food_removed_wrath_of_the_hive"));
    public static final GenericTrigger HONEY_BEE_LEGGINGS_FLOWER_POLLEN_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bee_leggings_flower_pollen"));
    public static final GenericTrigger HONEY_BUCKET_BEE_GROW_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_bee_grow"));
    public static final GenericTrigger HONEY_BUCKET_BEE_LOVE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_bee_love"));
    public static final GenericTrigger HONEY_BUCKET_BROOD_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_brood"));
    public static final GenericTrigger HONEY_BUCKET_POROUS_HONEYCOMB_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_porous_honeycomb"));
    public static final GenericTrigger HONEY_COCOON_SILK_TOUCH_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_cocoon_silk_touch"));
    public static final GenericTrigger HONEY_CRYSTAL_IN_WATER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_crystal_in_water"));
    public static final GenericTrigger HONEY_CRYSTAL_SHIELD_BLOCK_INEFFECTIVELY_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_crystal_shield_block_ineffectively"));
    public static final GenericTrigger HONEY_SLIME_CREATION_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_slime_creation"));
    public static final GenericTrigger HONEY_SLIME_HARVEST_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_slime_harvest"));
    public static final GenericTrigger POLLEN_PUFF_FIREBALL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_fireball"));
    public static final GenericTrigger POLLEN_PUFF_PANDA_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_panda"));
    public static final GenericTrigger POLLEN_PUFF_POLLINATED_BEE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_pollinate_bee"));
    public static final GenericTrigger POLLEN_PUFF_POLLINATED_TALL_FLOWER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_pollinate_tall_flower"));
    public static final EntitySpecificTrigger PROTECTION_OF_THE_HIVE_DEFENSE_TRIGGER = new EntitySpecificTrigger(new ResourceLocation(Bumblezone.MODID, "protection_of_the_hive_defense"));
    public static final GenericTrigger QUEEN_BEEHEMOTH_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "created_queen_beehemoth"));
    public static final RecipeDiscoveredTrigger RECIPE_DISCOVERED_TRIGGER = new RecipeDiscoveredTrigger();
    public static final GenericTrigger STINGER_SPEAR_LONG_RANGE_KILL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_long_range_kill"));
    public static final GenericTrigger STINGER_SPEAR_PARALYZE_BOSS_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_paralyze_boss"));
    public static final GenericTrigger STINGER_SPEAR_PARALYZING_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_paralyzing"));
    public static final GenericTrigger STINGER_SPEAR_POISONING_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_poisoning"));
    public static final GenericTrigger STINGLESS_BEE_HELMET_SUPER_SIGHT_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stingless_bee_helmet_super_sight"));
    public static final GenericTrigger SUGAR_WATER_NEXT_TO_SUGAR_CANE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "sugar_water_next_to_sugar_cane"));
    public static final GenericTrigger TELEPORT_OUT_OF_BUMBLEZONE_FALL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_out_of_bumblezone_fall"));
    public static final GenericTrigger TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_to_bumblezone_pearl"));
    public static final GenericTrigger TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_to_bumblezone_piston"));
    public static final GenericTrigger TRIGGER_REDSTONE_HONEY_WEB_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "trigger_redstone_honey_web"));

    public static void registerCriteriaTriggers() {
        CriteriaTriggers.register(BEENERGIZED_MAXED_TRIGGER);
        CriteriaTriggers.register(BEE_DROP_POLLEN_PUFF_TRIGGER);
        CriteriaTriggers.register(BUMBLE_BEE_CHESTPLATE_MAX_FLIGHT_TRIGGER);
        CriteriaTriggers.register(CLEANUP_HONEY_WEB_TRIGGER);
        CriteriaTriggers.register(CLEANUP_STICKY_HONEY_RESIDUE_TRIGGER);
        CriteriaTriggers.register(COMB_CUTTER_EXTRA_DROPS_TRIGGER);
        CriteriaTriggers.register(EXTENDED_WRATH_OF_THE_HIVE_TRIGGER);
        CriteriaTriggers.register(FALLING_ON_POLLEN_BLOCK_TRIGGER);
        CriteriaTriggers.register(FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER);
        CriteriaTriggers.register(HONEY_BEE_LEGGINGS_FLOWER_POLLEN_TRIGGER);
        CriteriaTriggers.register(HONEY_BUCKET_BEE_GROW_TRIGGER);
        CriteriaTriggers.register(HONEY_BUCKET_BEE_LOVE_TRIGGER);
        CriteriaTriggers.register(HONEY_BUCKET_BROOD_TRIGGER);
        CriteriaTriggers.register(HONEY_BUCKET_POROUS_HONEYCOMB_TRIGGER);
        CriteriaTriggers.register(HONEY_COCOON_SILK_TOUCH_TRIGGER);
        CriteriaTriggers.register(HONEY_CRYSTAL_IN_WATER_TRIGGER);
        CriteriaTriggers.register(HONEY_CRYSTAL_SHIELD_BLOCK_INEFFECTIVELY_TRIGGER);
        CriteriaTriggers.register(HONEY_SLIME_CREATION_TRIGGER);
        CriteriaTriggers.register(HONEY_SLIME_HARVEST_TRIGGER);
        CriteriaTriggers.register(POLLEN_PUFF_FIREBALL_TRIGGER);
        CriteriaTriggers.register(POLLEN_PUFF_PANDA_TRIGGER);
        CriteriaTriggers.register(POLLEN_PUFF_POLLINATED_BEE_TRIGGER);
        CriteriaTriggers.register(POLLEN_PUFF_POLLINATED_TALL_FLOWER_TRIGGER);
        CriteriaTriggers.register(PROTECTION_OF_THE_HIVE_DEFENSE_TRIGGER);
        CriteriaTriggers.register(QUEEN_BEEHEMOTH_TRIGGER);
        CriteriaTriggers.register(RECIPE_DISCOVERED_TRIGGER);
        CriteriaTriggers.register(STINGER_SPEAR_LONG_RANGE_KILL_TRIGGER);
        CriteriaTriggers.register(STINGER_SPEAR_PARALYZE_BOSS_TRIGGER);
        CriteriaTriggers.register(STINGER_SPEAR_PARALYZING_TRIGGER);
        CriteriaTriggers.register(STINGER_SPEAR_POISONING_TRIGGER);
        CriteriaTriggers.register(STINGLESS_BEE_HELMET_SUPER_SIGHT_TRIGGER);
        CriteriaTriggers.register(SUGAR_WATER_NEXT_TO_SUGAR_CANE_TRIGGER);
        CriteriaTriggers.register(TELEPORT_OUT_OF_BUMBLEZONE_FALL_TRIGGER);
        CriteriaTriggers.register(TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER);
        CriteriaTriggers.register(TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER);
        CriteriaTriggers.register(TRIGGER_REDSTONE_HONEY_WEB_TRIGGER);
    }
}

package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.advancements.BlockStateSpecificTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.CounterTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.EntitySpecificTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.GenericTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.ItemSpecificTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.KilledCounterTrigger;
import com.telepathicgrunt.the_bumblezone.advancements.TargetAdvancementDoneTrigger;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

public class BzCriterias {
    public static final ResourceLocation QUEENS_DESIRE_ROOT_ADVANCEMENT = new ResourceLocation(Bumblezone.MODID, "the_queens_desire/the_beginning");
    public static final ResourceLocation QUEENS_DESIRE_FINAL_ADVANCEMENT = new ResourceLocation(Bumblezone.MODID, "the_queens_desire/journeys_end");
    public static final ResourceLocation IS_NEAR_BEEHIVE_ADVANCEMENT = new ResourceLocation(Bumblezone.MODID, "teleportation/is_near_beehive");

    // CRITERIA TRIGGERS
    public static final CounterTrigger BEE_BREEDING_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "bee_breeding"));
    public static final GenericTrigger BEENERGIZED_MAXED_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "beenergized_maxed"));
    public static final GenericTrigger BEE_CANNON_FULL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bee_cannon_full"));
    public static final GenericTrigger BEE_DROP_POLLEN_PUFF_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bee_drop_pollen_puff"));
    public static final CounterTrigger BEE_FED_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "bee_fed"));
    public static final EntitySpecificTrigger BEE_HIT_WRATH_OF_THE_HIVE_TRIGGER = new EntitySpecificTrigger(new ResourceLocation(Bumblezone.MODID, "bee_hit_wrath_of_the_hive"));
    public static final CounterTrigger BEE_SAVED_BY_STINGER_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "bee_saved_by_stinger"));
    public static final GenericTrigger BEE_STINGER_PARALYZE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bee_stinger_paralyze"));
    public static final CounterTrigger BEE_STINGER_SHOOTER_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "bee_stinger_shooter"));
    public static final GenericTrigger BEE_QUEEN_FIRST_TRADE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bee_queen_first_trade"));
    public static final CounterTrigger BEE_QUEEN_TRADING_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "bee_queen_trading"));
    public static final CounterTrigger BEEHIVE_CRAFTED_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "beehive_crafted"));
    public static final GenericTrigger BUMBLE_BEE_CHESTPLATE_MAX_FLIGHT_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "bumble_bee_chestplate_max_flight"));
    public static final GenericTrigger BUZZING_BRIEFCASE_FULL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "buzzing_briefcase_full"));
    public static final GenericTrigger BUZZING_BRIEFCASE_HEAL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "buzzing_briefcase_heal"));
    public static final GenericTrigger BUZZING_BRIEFCASE_RELEASE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "buzzing_briefcase_release"));
    public static final GenericTrigger CARPENTER_BEE_BOOTS_MINED_BLOCKS_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "carpenter_bee_boots_mined_blocks"));
    public static final GenericTrigger CARPENTER_BEE_BOOTS_WALL_HANGING_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "carpenter_bee_boots_wall_hanging"));
    public static final BlockStateSpecificTrigger CARVE_WAX_TRIGGER = new BlockStateSpecificTrigger(new ResourceLocation(Bumblezone.MODID, "carve_wax"));
    public static final GenericTrigger CLEANUP_HONEY_WEB_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "cleanup_honey_web"));
    public static final GenericTrigger CLEANUP_STICKY_HONEY_RESIDUE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "cleanup_sticky_honey_residue"));
    public static final GenericTrigger COMB_CUTTER_EXTRA_DROPS_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "comb_cutter_extra_drops"));
    public static final GenericTrigger CRAFT_MULTI_POTION_POTION_CANDLE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "craft_multi_potion_potion_candle"));
    public static final GenericTrigger CRYSTAL_CANNON_FULL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "crystal_cannon_full"));
    public static final GenericTrigger ENCHANT_CRYSTALLINE_FLOWER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "enchant_crystalline_flower"));
    public static final ItemSpecificTrigger ESSENCE_EVENT_REWARD_TRIGGER = new ItemSpecificTrigger(new ResourceLocation(Bumblezone.MODID, "essence_event_reward"));
    public static final GenericTrigger EXTEND_STRING_CURTAIN_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "extend_string_curtain"));
    public static final GenericTrigger FALLING_ON_POLLEN_BLOCK_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "fall_onto_pollen_pile"));
    public static final GenericTrigger FLOWER_HEADWEAR_WRATH_STRUCTURE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "flower_headwear_wrath_structure"));
    public static final ItemSpecificTrigger FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER = new ItemSpecificTrigger(new ResourceLocation(Bumblezone.MODID, "food_removed_wrath_of_the_hive"));
    public static final GenericTrigger GETTING_PROTECTION_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "getting_protection"));
    public static final GenericTrigger GROW_CRYSTALLINE_FLOWER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "grow_crystalline_flower"));
    public static final GenericTrigger HONEY_BEE_LEGGINGS_FLOWER_POLLEN_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bee_leggings_flower_pollen"));
    public static final GenericTrigger HONEY_BEE_LEGGINGS_POLLEN_REMOVAL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bee_leggings_pollen_removal"));
    public static final CounterTrigger HONEY_BOTTLE_DRANK_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bottle_drank"));
    public static final GenericTrigger HONEY_BUCKET_BEE_GROW_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_bee_grow"));
    public static final GenericTrigger HONEY_BUCKET_BEE_LOVE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_bee_love"));
    public static final GenericTrigger HONEY_BUCKET_BROOD_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_bucket_brood"));
    public static final GenericTrigger HONEY_COCOON_SILK_TOUCH_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_cocoon_silk_touch"));
    public static final GenericTrigger HONEY_COMPASS_USE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_compass_use"));
    public static final GenericTrigger HONEY_CRYSTAL_SHIELD_BLOCK_INEFFECTIVELY_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_crystal_shield_block_ineffectively"));
    public static final GenericTrigger HONEY_PERMISSION_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_permission"));
    public static final CounterTrigger HONEY_SLIME_BRED_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "honey_slime_bred"));
    public static final GenericTrigger HONEY_SLIME_CREATION_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_slime_creation"));
    public static final GenericTrigger HONEY_SLIME_HARVEST_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "honey_slime_harvest"));
    public static final GenericTrigger IS_NEAR_BEEHIVE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "is_near_beehive"));
    public static final KilledCounterTrigger KILLED_COUNTER_TRIGGER = new KilledCounterTrigger(new ResourceLocation(Bumblezone.MODID, "killed_counter"));
    public static final GenericTrigger LIGHT_SOUL_SUPER_CANDLE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "light_soul_potion_candle"));
    public static final GenericTrigger POLLEN_PUFF_FIREBALL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_fireball"));
    public static final CounterTrigger POLLEN_PUFF_HIT_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_hit"));
    public static final GenericTrigger POLLEN_PUFF_MOOSHROOM_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_mooshroom"));
    public static final CounterTrigger POLLEN_PUFF_SPAWN_FLOWERS_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_spawn_flower"));
    public static final GenericTrigger POLLEN_PUFF_PANDA_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_panda"));
    public static final GenericTrigger POLLEN_PUFF_POLLINATED_BEE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_pollinate_bee"));
    public static final GenericTrigger POLLEN_PUFF_POLLINATED_TALL_FLOWER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "pollen_puff_pollinate_tall_flower"));
    public static final GenericTrigger PROJECTILE_LIGHT_INSTANT_POTION_CANDLE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "projectile_light_instant_potion_candle"));
    public static final EntitySpecificTrigger PROTECTION_OF_THE_HIVE_DEFENSE_TRIGGER = new EntitySpecificTrigger(new ResourceLocation(Bumblezone.MODID, "protection_of_the_hive_defense"));
    public static final GenericTrigger QUEEN_BEEHEMOTH_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "created_queen_beehemoth"));
    public static final GenericTrigger ROOTMIN_FLOWER_SWAP_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "rootmin_flower_swap"));
    public static final GenericTrigger ROYAL_JELLY_BLOCK_PISTON_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "royal_jelly_block_piston"));
    public static final GenericTrigger SEMPITERNAL_SANCTUM_ENTER_WITH_BEE_ESSENCE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "sempiternal_sanctum_enter_with_bee_essence"));
    public static final GenericTrigger SENTRY_WATCHER_SPAWN_EGG_USED_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "sentry_watcher_spawn_egg_used"));
    public static final GenericTrigger STINGER_SPEAR_KILLED_WITH_WITHER_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_killed_with_wither"));
    public static final GenericTrigger STINGER_SPEAR_LONG_RANGE_KILL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_long_range_kill"));
    public static final GenericTrigger STINGER_SPEAR_PARALYZE_BOSS_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_paralyze_boss"));
    public static final GenericTrigger STINGER_SPEAR_PARALYZING_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_paralyzing"));
    public static final GenericTrigger STINGER_SPEAR_POISONING_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stinger_spear_poisoning"));
    public static final GenericTrigger STINGLESS_BEE_HELMET_SUPER_SIGHT_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "stingless_bee_helmet_super_sight"));
    public static final GenericTrigger SUGAR_WATER_NEXT_TO_SUGAR_CANE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "sugar_water_next_to_sugar_cane"));
    public static final TargetAdvancementDoneTrigger TARGET_ADVANCEMENT_DONE_TRIGGER = new TargetAdvancementDoneTrigger(new ResourceLocation(Bumblezone.MODID, "target_advancement_done"));
    public static final GenericTrigger TELEPORT_OUT_OF_BUMBLEZONE_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_out_of_bumblezone"));
    public static final GenericTrigger TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_to_bumblezone_pearl"));
    public static final GenericTrigger TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "teleport_to_bumblezone_piston"));
    public static final GenericTrigger TRIGGER_REDSTONE_HONEY_WEB_TRIGGER = new GenericTrigger(new ResourceLocation(Bumblezone.MODID, "trigger_redstone_honey_web"));
    public static final CounterTrigger VARIANT_BEE_BRIEFCASE_CAPTURE_TRIGGER = new CounterTrigger(new ResourceLocation(Bumblezone.MODID, "variant_bee_briefcase_capture"));

    public static void registerCriteriaTriggers() {
        register(BEE_BREEDING_TRIGGER);
        register(BEENERGIZED_MAXED_TRIGGER);
        register(BEE_CANNON_FULL_TRIGGER);
        register(BEE_DROP_POLLEN_PUFF_TRIGGER);
        register(BEE_FED_TRIGGER);
        register(BEE_SAVED_BY_STINGER_TRIGGER);
        register(BEE_STINGER_PARALYZE_TRIGGER);
        register(BEE_STINGER_SHOOTER_TRIGGER);
        register(BEE_QUEEN_FIRST_TRADE_TRIGGER);
        register(BEEHIVE_CRAFTED_TRIGGER);
        register(BUMBLE_BEE_CHESTPLATE_MAX_FLIGHT_TRIGGER);
        register(BUZZING_BRIEFCASE_FULL_TRIGGER);
        register(BUZZING_BRIEFCASE_HEAL_TRIGGER);
        register(BUZZING_BRIEFCASE_RELEASE_TRIGGER);
        register(CARPENTER_BEE_BOOTS_MINED_BLOCKS_TRIGGER);
        register(CARPENTER_BEE_BOOTS_WALL_HANGING_TRIGGER);
        register(CARVE_WAX_TRIGGER);
        register(CLEANUP_HONEY_WEB_TRIGGER);
        register(CLEANUP_STICKY_HONEY_RESIDUE_TRIGGER);
        register(COMB_CUTTER_EXTRA_DROPS_TRIGGER);
        register(CRAFT_MULTI_POTION_POTION_CANDLE_TRIGGER);
        register(CRYSTAL_CANNON_FULL_TRIGGER);
        register(BEE_HIT_WRATH_OF_THE_HIVE_TRIGGER);
        register(ENCHANT_CRYSTALLINE_FLOWER_TRIGGER);
        register(ESSENCE_EVENT_REWARD_TRIGGER);
        register(EXTEND_STRING_CURTAIN_TRIGGER);
        register(FALLING_ON_POLLEN_BLOCK_TRIGGER);
        register(FLOWER_HEADWEAR_WRATH_STRUCTURE_TRIGGER);
        register(FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER);
        register(GETTING_PROTECTION_TRIGGER);
        register(GROW_CRYSTALLINE_FLOWER_TRIGGER);
        register(HONEY_BEE_LEGGINGS_FLOWER_POLLEN_TRIGGER);
        register(HONEY_BEE_LEGGINGS_POLLEN_REMOVAL_TRIGGER);
        register(HONEY_BOTTLE_DRANK_TRIGGER);
        register(HONEY_BUCKET_BEE_GROW_TRIGGER);
        register(HONEY_BUCKET_BEE_LOVE_TRIGGER);
        register(HONEY_BUCKET_BROOD_TRIGGER);
        register(HONEY_COCOON_SILK_TOUCH_TRIGGER);
        register(HONEY_COMPASS_USE_TRIGGER);
        register(HONEY_CRYSTAL_SHIELD_BLOCK_INEFFECTIVELY_TRIGGER);
        register(HONEY_PERMISSION_TRIGGER);
        register(HONEY_SLIME_BRED_TRIGGER);
        register(HONEY_SLIME_CREATION_TRIGGER);
        register(HONEY_SLIME_HARVEST_TRIGGER);
        register(IS_NEAR_BEEHIVE_TRIGGER);
        register(KILLED_COUNTER_TRIGGER);
        register(LIGHT_SOUL_SUPER_CANDLE_TRIGGER);
        register(POLLEN_PUFF_MOOSHROOM_TRIGGER);
        register(POLLEN_PUFF_FIREBALL_TRIGGER);
        register(POLLEN_PUFF_HIT_TRIGGER);
        register(POLLEN_PUFF_PANDA_TRIGGER);
        register(POLLEN_PUFF_POLLINATED_BEE_TRIGGER);
        register(POLLEN_PUFF_POLLINATED_TALL_FLOWER_TRIGGER);
        register(POLLEN_PUFF_SPAWN_FLOWERS_TRIGGER);
        register(PROJECTILE_LIGHT_INSTANT_POTION_CANDLE_TRIGGER);
        register(PROTECTION_OF_THE_HIVE_DEFENSE_TRIGGER);
        register(BEE_QUEEN_TRADING_TRIGGER);
        register(QUEEN_BEEHEMOTH_TRIGGER);
        register(ROOTMIN_FLOWER_SWAP_TRIGGER);
        register(ROYAL_JELLY_BLOCK_PISTON_TRIGGER);
        register(SEMPITERNAL_SANCTUM_ENTER_WITH_BEE_ESSENCE_TRIGGER);
        register(SENTRY_WATCHER_SPAWN_EGG_USED_TRIGGER);
        register(STINGER_SPEAR_KILLED_WITH_WITHER_TRIGGER);
        register(STINGER_SPEAR_LONG_RANGE_KILL_TRIGGER);
        register(STINGER_SPEAR_PARALYZE_BOSS_TRIGGER);
        register(STINGER_SPEAR_PARALYZING_TRIGGER);
        register(STINGER_SPEAR_POISONING_TRIGGER);
        register(STINGLESS_BEE_HELMET_SUPER_SIGHT_TRIGGER);
        register(SUGAR_WATER_NEXT_TO_SUGAR_CANE_TRIGGER);
        register(TARGET_ADVANCEMENT_DONE_TRIGGER);
        register(TELEPORT_OUT_OF_BUMBLEZONE_TRIGGER);
        register(TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER);
        register(TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER);
        register(TRIGGER_REDSTONE_HONEY_WEB_TRIGGER);
        register(VARIANT_BEE_BRIEFCASE_CAPTURE_TRIGGER);
    }

    @ExpectPlatform
    private static <T extends CriterionTrigger<?>> T register(T criterionTrigger) {
        throw new NotImplementedException("CriterionTriggerRegistry.register");
    }
}

package com.telepathicgrunt.the_bumblezone.modinit;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.AbilityEssenceAbilityData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.AbilityEssenceActivityData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.AbilityEssenceCooldownData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.AbilityEssenceLastChargeData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.BumbleBeeChestplateData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.CarpenterBeeBootsHangingData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.CarpenterBeeBootsMiningData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.CrystalCannonData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.CrystallineFlowerData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyBeeLeggingsData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassBaseData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassStateData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassTargetData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCrystalShieldCurrentLevelData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCrystalShieldDefinedLevelsData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.KnowingEssenceStructureData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.RagingEssenceCurrentTargetData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.RagingEssenceStateData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.RagingEssenceTimerData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.StinglessBeeHelmetData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.CustomData;

public class BzDataComponents {

    public static final ResourcefulRegistry<DataComponentType<?>> DATA_COMPONENT_TYPE = ResourcefulRegistries.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Bumblezone.MODID);

    public static final RegistryEntry<DataComponentType<CrystallineFlowerData>> CRYSTALLINE_FLOWER_DATA = DATA_COMPONENT_TYPE.register(
            "crystalline_flower_data", () -> buildSyncPersistentComponentRegistryFriendly(CrystallineFlowerData.DIRECT_CODEC, CrystallineFlowerData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<StinglessBeeHelmetData>> STINGLESS_BEE_HELMET_DATA = DATA_COMPONENT_TYPE.register(
            "stingless_bee_helmet_data", () -> buildSyncPersistentComponentRegistryFriendly(StinglessBeeHelmetData.DIRECT_CODEC, StinglessBeeHelmetData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<BumbleBeeChestplateData>> BUMBLEBEE_CHESTPLATE_DATA = DATA_COMPONENT_TYPE.register(
            "bumblebee_chestplate_data", () -> buildSyncPersistentComponentRegistryFriendly(BumbleBeeChestplateData.DIRECT_CODEC, BumbleBeeChestplateData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<CarpenterBeeBootsMiningData>> CARPENTER_BEE_BOOTS_MINING_DATA = DATA_COMPONENT_TYPE.register(
            "carpenter_bee_boots_mining_data", () -> buildPersistentComponent(CarpenterBeeBootsMiningData.DIRECT_CODEC));

    public static final RegistryEntry<DataComponentType<CarpenterBeeBootsHangingData>> CARPENTER_BEE_BOOTS_HANGING_DATA = DATA_COMPONENT_TYPE.register(
            "carpenter_bee_boots_hanging_data", () -> buildSyncPersistentComponentRegistryFriendly(CarpenterBeeBootsHangingData.DIRECT_CODEC, CarpenterBeeBootsHangingData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyBeeLeggingsData>> HONEY_BEE_LEGGINGS_DATA = DATA_COMPONENT_TYPE.register(
            "honey_bee_leggings_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyBeeLeggingsData.DIRECT_CODEC, HoneyBeeLeggingsData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyCrystalShieldCurrentLevelData>> HONEY_CRYSTAL_SHIELD_CURRENT_LEVEL_DATA = DATA_COMPONENT_TYPE.register(
            "honey_crystal_shield_current_level_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyCrystalShieldCurrentLevelData.DIRECT_CODEC, HoneyCrystalShieldCurrentLevelData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyCrystalShieldDefinedLevelsData>> HONEY_CRYSTAL_SHIELD_DEFINED_LEVELS_DATA = DATA_COMPONENT_TYPE.register(
            "honey_crystal_shield_defined_levels_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyCrystalShieldDefinedLevelsData.DIRECT_CODEC, HoneyCrystalShieldDefinedLevelsData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<CrystalCannonData>> CRYSTAL_CANNON_DATA = DATA_COMPONENT_TYPE.register(
            "crystal_cannon_data", () -> buildSyncPersistentComponentRegistryFriendly(CrystalCannonData.DIRECT_CODEC, CrystalCannonData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<CustomData>> BEE_CANNON_DATA = DATA_COMPONENT_TYPE.register(
            "bee_cannon_data", () -> buildSyncPersistentComponent(CustomData.CODEC, CustomData.STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<CustomData>> BUZZING_BRIEFCASE_DATA = DATA_COMPONENT_TYPE.register(
            "buzzing_briefcase_data", () -> buildSyncPersistentComponent(CustomData.CODEC, CustomData.STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyCompassBaseData>> HONEY_COMPASS_BASE_DATA = DATA_COMPONENT_TYPE.register(
            "honey_compass_base_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyCompassBaseData.DIRECT_CODEC, HoneyCompassBaseData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyCompassStateData>> HONEY_COMPASS_STATE_DATA = DATA_COMPONENT_TYPE.register(
            "honey_compass_state_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyCompassStateData.DIRECT_CODEC, HoneyCompassStateData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<HoneyCompassTargetData>> HONEY_COMPASS_TARGET_DATA = DATA_COMPONENT_TYPE.register(
            "honey_compass_target_data", () -> buildSyncPersistentComponentRegistryFriendly(HoneyCompassTargetData.DIRECT_CODEC, HoneyCompassTargetData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<AbilityEssenceAbilityData>> ABILITY_ESSENCE_ABILITY_DATA = DATA_COMPONENT_TYPE.register(
            "ability_essence_ability_data", () -> buildSyncPersistentComponentRegistryFriendly(AbilityEssenceAbilityData.DIRECT_CODEC, AbilityEssenceAbilityData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<AbilityEssenceActivityData>> ABILITY_ESSENCE_ACTIVITY_DATA = DATA_COMPONENT_TYPE.register(
            "ability_essence_activity_data", () -> buildSyncPersistentComponentRegistryFriendly(AbilityEssenceActivityData.DIRECT_CODEC, AbilityEssenceActivityData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<AbilityEssenceLastChargeData>> ABILITY_ESSENCE_LAST_CHARGE_DATA = DATA_COMPONENT_TYPE.register(
            "ability_essence_last_charge_data", () -> buildSyncPersistentComponentRegistryFriendly(AbilityEssenceLastChargeData.DIRECT_CODEC, AbilityEssenceLastChargeData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<AbilityEssenceCooldownData>> ABILITY_ESSENCE_COOLDOWN_DATA = DATA_COMPONENT_TYPE.register(
            "ability_essence_cooldown_data", () -> buildSyncPersistentComponentRegistryFriendly(AbilityEssenceCooldownData.DIRECT_CODEC, AbilityEssenceCooldownData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<KnowingEssenceStructureData>> KNOWING_ESSENCE_STRUCTURE_DATA = DATA_COMPONENT_TYPE.register(
            "knowing_essence_structure_data", () -> buildSyncPersistentComponentRegistryFriendly(KnowingEssenceStructureData.DIRECT_CODEC, KnowingEssenceStructureData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<RagingEssenceStateData>> RAGING_ESSENCE_STATE_DATA = DATA_COMPONENT_TYPE.register(
            "raging_essence_state_data", () -> buildSyncPersistentComponentRegistryFriendly(RagingEssenceStateData.DIRECT_CODEC, RagingEssenceStateData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<RagingEssenceCurrentTargetData>> RAGING_ESSENCE_CURRENT_TARGET_DATA = DATA_COMPONENT_TYPE.register(
            "raging_essence_current_target_data", () -> buildSyncPersistentComponentRegistryFriendly(RagingEssenceCurrentTargetData.DIRECT_CODEC, RagingEssenceCurrentTargetData.DIRECT_STREAM_CODEC));

    public static final RegistryEntry<DataComponentType<RagingEssenceTimerData>> RAGING_ESSENCE_TIMER_DATA = DATA_COMPONENT_TYPE.register(
            "raging_essence_timer_data", () -> buildPersistentComponent(RagingEssenceTimerData.DIRECT_CODEC));

    private static <T> DataComponentType<T> buildPersistentComponent(Codec<T> directCodec) {
        return DataComponentType.<T>builder().persistent(directCodec).cacheEncoding().build();
    }

    private static <T> DataComponentType<T> buildSyncPersistentComponentRegistryFriendly(Codec<T> directCodec, StreamCodec<RegistryFriendlyByteBuf, T> directStreamCodec) {
        return DataComponentType.<T>builder().persistent(directCodec).networkSynchronized(directStreamCodec).cacheEncoding().build();
    }

    private static <T> DataComponentType<T> buildSyncPersistentComponent(Codec<T> directCodec, StreamCodec<ByteBuf, T> directStreamCodec) {
        return DataComponentType.<T>builder().persistent(directCodec).networkSynchronized(directStreamCodec).cacheEncoding().build();
    }
}

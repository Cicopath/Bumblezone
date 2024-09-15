package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.living.CosmicCrystalEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeeQueenEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeehemothEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.HoneySlimeEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.RootminEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.VariantBeeEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.BeeStingerEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.DirtPelletEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ElectricRingEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.HoneyCrystalShardEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.PollenPuffEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.PurpleSpikeEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.SentryWatcherEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ThrownStingerSpearEntity;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterEntityAttributesEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.RegisterSpawnPlacementsEvent;
import com.telepathicgrunt.the_bumblezone.modinit.registry.RegistryEntry;
import com.telepathicgrunt.the_bumblezone.modinit.registry.ResourcefulRegistries;
import com.telepathicgrunt.the_bumblezone.modinit.registry.ResourcefulRegistry;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.levelgen.Heightmap;

public class BzEntities {
    public static final ResourcefulRegistry<EntityType<?>> ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, Bumblezone.MODID);

    public static final RegistryEntry<EntityType<VariantBeeEntity>> VARIANT_BEE = ENTITIES.register("variant_bee", () -> PlatformHooks.createEntityType(VariantBeeEntity::new, MobCategory.CREATURE, 0.7f, 0.6f, true, 8, 3, "variant_bee"));
    public static final RegistryEntry<EntityType<HoneySlimeEntity>> HONEY_SLIME = ENTITIES.register("honey_slime", () -> PlatformHooks.createEntityType(HoneySlimeEntity::new, MobCategory.CREATURE, 1F, true, 8, 3, "honey_slime"));
    public static final RegistryEntry<EntityType<BeehemothEntity>> BEEHEMOTH = ENTITIES.register("beehemoth", () -> PlatformHooks.createEntityType(BeehemothEntity::new, MobCategory.CREATURE, 1.2F, false, 16, 3, "beehemoth"));
    public static final RegistryEntry<EntityType<BeeQueenEntity>> BEE_QUEEN = ENTITIES.register("bee_queen", () -> PlatformHooks.createEntityType(BeeQueenEntity::new, MobCategory.CREATURE, 2.9F, false, 16, 3, "bee_queen"));
    public static final RegistryEntry<EntityType<RootminEntity>> ROOTMIN = ENTITIES.register("rootmin", () -> PlatformHooks.createEntityType(RootminEntity::new, MobCategory.MONSTER, 1F, 1.56f, true, 8, 3, "rootmin"));
    public static final RegistryEntry<EntityType<SentryWatcherEntity>> SENTRY_WATCHER = ENTITIES.register("sentry_watcher", () -> PlatformHooks.createEntityType(SentryWatcherEntity::new, MobCategory.MISC, 1.99F, 1.45f, true, 16, 3, "sentry_watcher"));
    public static final RegistryEntry<EntityType<PollenPuffEntity>> POLLEN_PUFF_ENTITY = ENTITIES.register("pollen_puff", () -> PlatformHooks.createEntityType(PollenPuffEntity::new, MobCategory.MISC, 0.25F, false, 4, 10, "pollen_puff"));
    public static final RegistryEntry<EntityType<DirtPelletEntity>> DIRT_PELLET_ENTITY = ENTITIES.register("dirt_pellet", () -> PlatformHooks.createEntityType(DirtPelletEntity::new, MobCategory.MISC, 0.5F, false, 4, 10, "dirt_pellet"));
    public static final RegistryEntry<EntityType<ThrownStingerSpearEntity>> THROWN_STINGER_SPEAR_ENTITY = ENTITIES.register("thrown_stinger_spear", () -> PlatformHooks.createEntityType(ThrownStingerSpearEntity::new, MobCategory.MISC, 0.5F, false, 4, 20, "thrown_stinger_spear"));
    public static final RegistryEntry<EntityType<BeeStingerEntity>> BEE_STINGER_ENTITY = ENTITIES.register("bee_stinger", () -> PlatformHooks.createEntityType(BeeStingerEntity::new, MobCategory.MISC, 0.5F, false, 4, 20, "bee_stinger"));
    public static final RegistryEntry<EntityType<HoneyCrystalShardEntity>> HONEY_CRYSTAL_SHARD = ENTITIES.register("honey_crystal_shard", () -> PlatformHooks.createEntityType(HoneyCrystalShardEntity::new, MobCategory.MISC, 0.5F, false, 4, 20, "honey_crystal_shard"));
    public static final RegistryEntry<EntityType<ElectricRingEntity>> ELECTRIC_RING_ENTITY = ENTITIES.register("electric_ring_entity", () -> PlatformHooks.createEntityType(ElectricRingEntity::new, MobCategory.MISC, 3F, true, 4, 20, "electric_ring_entity"));
    public static final RegistryEntry<EntityType<PurpleSpikeEntity>> PURPLE_SPIKE_ENTITY = ENTITIES.register("purple_spike_entity", () -> PlatformHooks.createEntityType(PurpleSpikeEntity::new, MobCategory.MISC, 1F, true, 4, 20, "purple_spike_entity"));
    public static final RegistryEntry<EntityType<CosmicCrystalEntity>> COSMIC_CRYSTAL_ENTITY = ENTITIES.register("cosmic_crystal_entity", () -> PlatformHooks.createEntityType(CosmicCrystalEntity::new, MobCategory.MISC, 1F, 2f, true, 8, 3, "cosmic_crystal_entity"));

    public static void registerEntitySpawnRestrictions(RegisterSpawnPlacementsEvent event) {
        event.register(HONEY_SLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        event.register(BEEHEMOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeehemothEntity::checkMobSpawnRules);
        event.register(BEE_QUEEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeeQueenEntity::checkMobSpawnRules);
        event.register(ROOTMIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    public static void registerEntityAttributes(RegisterEntityAttributesEvent event) {
        event.register(VARIANT_BEE.get(), Bee.createAttributes());
        event.register(HONEY_SLIME.get(), HoneySlimeEntity.getAttributeBuilder());
        event.register(BEEHEMOTH.get(), BeehemothEntity.getAttributeBuilder());
        event.register(BEE_QUEEN.get(), BeeQueenEntity.getAttributeBuilder());
        event.register(ROOTMIN.get(), RootminEntity.getAttributeBuilder());
        event.register(COSMIC_CRYSTAL_ENTITY.get(), CosmicCrystalEntity.getAttributeBuilder());
    }
}

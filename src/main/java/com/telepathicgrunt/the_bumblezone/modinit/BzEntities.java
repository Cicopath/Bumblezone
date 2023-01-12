package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeeQueenEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeehemothEntity;
import com.telepathicgrunt.the_bumblezone.entities.mobs.HoneySlimeEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.BeeStingerEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.HoneyCrystalShardEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.PollenPuffEntity;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ThrownStingerSpearEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;

public class BzEntities {

    public static final EntityType<HoneySlimeEntity> HONEY_SLIME = FabricEntityTypeBuilder
            .<HoneySlimeEntity>createMob()
            .spawnGroup(MobCategory.CREATURE)
            .entityFactory(HoneySlimeEntity::new)
            .defaultAttributes(HoneySlimeEntity::getAttributeBuilder)
            .dimensions(EntityDimensions.fixed(1F, 1F))
            .trackRangeChunks(8)
            .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
            .build();

    public static final EntityType<BeehemothEntity> BEEHEMOTH = FabricEntityTypeBuilder
            .<BeehemothEntity>createMob()
            .spawnGroup(MobCategory.CREATURE)
            .entityFactory(BeehemothEntity::new)
            .defaultAttributes(BeehemothEntity::getAttributeBuilder)
            .dimensions(EntityDimensions.fixed(1.2F, 1.2F))
            .trackRangeChunks(16)
            .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeehemothEntity::checkMobSpawnRules)
            .build();

    public static final EntityType<BeeQueenEntity> BEE_QUEEN = FabricEntityTypeBuilder
            .<BeeQueenEntity>createMob()
            .spawnGroup(MobCategory.CREATURE)
            .entityFactory(BeeQueenEntity::new)
            .defaultAttributes(BeeQueenEntity::getAttributeBuilder)
            .dimensions(EntityDimensions.fixed(2.9F, 2.9F))
            .trackRangeChunks(16)
            .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeeQueenEntity::checkMobSpawnRules)
            .build();

    public static final EntityType<PollenPuffEntity> POLLEN_PUFF_ENTITY = FabricEntityTypeBuilder
            .<PollenPuffEntity>create(MobCategory.MISC, PollenPuffEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeChunks(4)
            .trackedUpdateRate(10)
            .build();

    public static final EntityType<ThrownStingerSpearEntity> THROWN_STINGER_SPEAR_ENTITY = FabricEntityTypeBuilder.
            <ThrownStingerSpearEntity>create(MobCategory.MISC, ThrownStingerSpearEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build();

    public static final EntityType<BeeStingerEntity> BEE_STINGER_ENTITY = FabricEntityTypeBuilder.
            <BeeStingerEntity>create(MobCategory.MISC, BeeStingerEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build();

    public static final EntityType<HoneyCrystalShardEntity> HONEY_CRYSTAL_SHARD = FabricEntityTypeBuilder.
            <HoneyCrystalShardEntity>create(MobCategory.MISC, HoneyCrystalShardEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build();

    // Stored here instead of BzItems to prevent classloading crash with Lithium in 1.19.3.
    // Somehow Lithium caused a cascade of classloading that then classloaded BzEntities too early in
    // Bootstrap which then crashes the Fabric/Quilt entity type builders.
    public static final Item HONEY_SLIME_SPAWN_EGG = new SpawnEggItem(BzEntities.HONEY_SLIME, 0xFFCC00, 0xFCA800, (new Item.Properties()));
    public static final Item BEEHEMOTH_SPAWN_EGG = new SpawnEggItem(BzEntities.BEEHEMOTH, 0xFFCA47, 0x68372A, (new Item.Properties()));
    public static final Item BEE_QUEEN_SPAWN_EGG = new SpawnEggItem(BzEntities.BEE_QUEEN, 0xFFFFFF, 0xFFFFFF, (new Item.Properties().rarity(Rarity.EPIC)));

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "honey_slime"), HONEY_SLIME);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "beehemoth"), BEEHEMOTH);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "bee_queen"), BEE_QUEEN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "pollen_puff"), POLLEN_PUFF_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "thrown_stinger_spear"), THROWN_STINGER_SPEAR_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "bee_stinger"), BEE_STINGER_ENTITY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Bumblezone.MODID, "honey_crystal_shard"), HONEY_CRYSTAL_SHARD);

        EntityDataSerializers.registerSerializer(BeeQueenEntity.QUEEN_POSE_SERIALIZER);
    }
}

package net.telepathicgrunt.bumblezone.features;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.telepathicgrunt.bumblezone.utils.RegUtils;

public class BzFeatures
{
    public static Feature<NoFeatureConfig> HONEYCOMB_HOLE = new HoneycombHole(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> HONEYCOMB_CAVES = new HoneycombCaves(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> CAVE_SUGAR_WATERFALL = new CaveSugarWaterfall(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> BEE_DUNGEON = new BeeDungeon(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> SPIDER_INFESTED_BEE_DUNGEON = new SpiderInfestedBeeDungeon(NoFeatureConfig::deserialize);
    public static Feature<NoFeatureConfig> HONEY_CRYSTAL_FEATURE = new HoneyCrystalFeature(NoFeatureConfig::deserialize);

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event)
    {
    	IForgeRegistry<Feature<?>> registry = event.getRegistry();
    	RegUtils.register(registry, HONEYCOMB_HOLE, "honeycomb_hole");
    	RegUtils.register(registry, HONEYCOMB_CAVES, "honeycomb_caves");
    	RegUtils.register(registry, CAVE_SUGAR_WATERFALL, "cave_sugar_waterfall");
    	RegUtils.register(registry, BEE_DUNGEON, "bee_dungeon");
    	RegUtils.register(registry, SPIDER_INFESTED_BEE_DUNGEON, "spider_infested_bee_dungeon");
    	RegUtils.register(registry, HONEY_CRYSTAL_FEATURE, "honey_crystal_feature");
    }
}

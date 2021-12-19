package com.telepathicgrunt.the_bumblezone.modinit;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class BzBiomeHeightRegistry {
    private BzBiomeHeightRegistry() {}

    public static final ResourceKey<Registry<BiomeTerrain>> BIOME_HEIGHT_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Bumblezone.MODID, "biome_height"));
    public static final Registry<BiomeTerrain> BIOME_HEIGHT_REGISTRY = FabricRegistryBuilder.createSimple(BiomeTerrain.class, BIOME_HEIGHT_KEY.location()).buildAndRegister();

    public static void initBiomeHeightRegistry() {
        Optional<? extends Registry<?>> registryOptional = Registry.REGISTRY.getOptional(BIOME_HEIGHT_KEY.location());
        registryOptional.ifPresent(registry -> {
            Registry.register((Registry<BiomeTerrain>)registry,
                    new ResourceLocation(Bumblezone.MODID, "hive_pillar"), new BiomeTerrain(15, 0));
            Registry.register((Registry<BiomeTerrain>)registry,
                    new ResourceLocation(Bumblezone.MODID, "hive_wall"), new BiomeTerrain(8, 0.1f));
            Registry.register((Registry<BiomeTerrain>)registry,
                    new ResourceLocation(Bumblezone.MODID, "pollinated_fields"), new BiomeTerrain(5, 0.8f));
            Registry.register((Registry<BiomeTerrain>)registry,
                    new ResourceLocation(Bumblezone.MODID, "pollinated_pillar"), new BiomeTerrain(13, 0));
            Registry.register((Registry<BiomeTerrain>)registry,
                    new ResourceLocation(Bumblezone.MODID, "sugar_water_floor"), new BiomeTerrain(-3.5f, 0));
        });
    }

    public static class BiomeTerrain{
        public final float depth;
        public final float weightModifier;

        public BiomeTerrain(float depth, float weightModifier) {
            this.depth = depth;
            this.weightModifier = weightModifier;
        }
    }
}

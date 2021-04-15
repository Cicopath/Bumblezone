package net.telepathicgrunt.bumblezone.world.dimension.layer;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.gen.ChunkRandom;
import net.telepathicgrunt.bumblezone.Bumblezone;
import net.telepathicgrunt.bumblezone.world.dimension.BzBiomeProvider;

import java.util.stream.IntStream;


public enum BzBiomeLayer implements InitLayer {
    INSTANCE;

    private static final Identifier SUGAR_WATER = new Identifier(Bumblezone.MODID, "sugar_water_floor");
    private static final Identifier HIVE_WALL = new Identifier(Bumblezone.MODID, "hive_wall");

    private static OctaveSimplexNoiseSampler perlinGen;
//	private double max = -100;
//	private double min = 100;


    public int sample(LayerRandomnessSource noise, int x, int z) {
        double perlinNoise = perlinGen.sample((double) x * 0.1D, (double) z * 0.0001D, false);
//
//		max = Math.max(max, perlinNoise);
//		min = Math.min(min, perlinNoise);
//		Bumblezone.LOGGER.log(Level.INFO, "Max: " + max +", Min: "+min + ", perlin: "+perlinNoise);

        if (Math.abs(perlinNoise) % 0.1D < 0.07D) {
            return BzBiomeProvider.layersBiomeRegistry.getRawId(BzBiomeProvider.layersBiomeRegistry.get(HIVE_WALL));
        }
        else {
            return BzBiomeProvider.layersBiomeRegistry.getRawId(BzBiomeProvider.layersBiomeRegistry.get(SUGAR_WATER));
        }
    }


    public static void setSeed(long seed) {
        if (perlinGen == null) {
            ChunkRandom sharedseedrandom = new ChunkRandom(seed);
            perlinGen = new OctaveSimplexNoiseSampler(sharedseedrandom, IntStream.rangeClosed(-1, 0));
        }
    }
}
package com.telepathicgrunt.bumblezone.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.bumblezone.Bumblezone;
import com.telepathicgrunt.bumblezone.mixin.NoiseChunkAccessor;
import com.telepathicgrunt.bumblezone.mixin.world.NoiseGeneratorSettingsInvoker;
import com.telepathicgrunt.bumblezone.modinit.BzEntities;
import com.telepathicgrunt.bumblezone.utils.BzPlacingUtils;
import com.telepathicgrunt.bumblezone.utils.WorldSeedHolder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import net.minecraft.world.level.levelgen.material.WorldGenMaterialRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class BzChunkGenerator extends ChunkGenerator {

    public static void registerChunkgenerator() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Bumblezone.MODID, "chunk_generator"), BzChunkGenerator.CODEC);
    }

    public static final Codec<BzChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter(bzChunkGenerator -> bzChunkGenerator.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(bzChunkGenerator -> bzChunkGenerator.biomeSource),
            Codec.LONG.fieldOf("seed").orElseGet(WorldSeedHolder::getSeed).stable().forGetter(bzChunkGenerator -> bzChunkGenerator.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(bzChunkGenerator -> bzChunkGenerator.settings))
    .apply(instance, instance.stable(BzChunkGenerator::new)));

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState[] EMPTY_COLUMN = new BlockState[0];
    protected final BlockState defaultBlock;
    private final Registry<NormalNoise.NoiseParameters> noises;
    private final long seed;
    protected final Supplier<NoiseGeneratorSettings> settings;
    private final NoiseSampler sampler;
    private final SurfaceSystem surfaceSystem;
    private final WorldGenMaterialRule materialRule;
    private final Aquifer.FluidPicker globalFluidPicker;
    private static final MobSpawnSettings.SpawnerData INITIAL_HONEY_SLIME_ENTRY = new MobSpawnSettings.SpawnerData(BzEntities.HONEY_SLIME, 1, 1, 3);
    private static final MobSpawnSettings.SpawnerData INITIAL_BEE_ENTRY = new MobSpawnSettings.SpawnerData(EntityType.BEE, 1, 1, 4);
    private static final MobSpawnSettings.SpawnerData INITIAL_BEEHEMOTH_ENTRY = new MobSpawnSettings.SpawnerData(BzEntities.BEEHEMOTH, 1, 1, 1);

    public BzChunkGenerator(Registry<NormalNoise.NoiseParameters> registry, BiomeSource biomeSource, long l, Supplier<NoiseGeneratorSettings> supplier) {
        this(registry, biomeSource, biomeSource, l, supplier);
    }

    private BzChunkGenerator(Registry<NormalNoise.NoiseParameters> registry, BiomeSource biomeSource, BiomeSource biomeSource2, long l, Supplier<NoiseGeneratorSettings> supplier) {
        super(biomeSource, biomeSource2, supplier.get().structureSettings(), l);
        this.noises = registry;
        this.seed = l;
        this.settings = supplier;
        NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
        this.defaultBlock = noiseGeneratorSettings.getDefaultBlock();
        NoiseSettings noiseSettings = noiseGeneratorSettings.noiseSettings();
        this.sampler = new NoiseSampler(noiseSettings, noiseGeneratorSettings.isNoiseCavesEnabled(), l, registry, noiseGeneratorSettings.getRandomSource());
        ImmutableList.Builder<WorldGenMaterialRule> builder = ImmutableList.builder();
        builder.add((noiseChunk, x, y, z) -> ((NoiseChunkAccessor)noiseChunk).thebumblezone_callUpdateNoiseAndGenerateBaseState(x, y, z));
        builder.add((noiseChunk, x, y, z) -> ((NoiseChunkAccessor)noiseChunk).thebumblezone_callOreVeinify(x, y, z));
        this.materialRule = new MaterialRuleList(builder.build());
        Aquifer.FluidStatus fluidStatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int i = noiseGeneratorSettings.seaLevel();
        Aquifer.FluidStatus fluidStatus2 = new Aquifer.FluidStatus(i, noiseGeneratorSettings.getDefaultFluid());
        this.globalFluidPicker = (j, k, lx) -> k < Math.min(-54, i) ? fluidStatus : fluidStatus2;
        this.surfaceSystem = new SurfaceSystem(registry, this.defaultBlock, i, l, noiseGeneratorSettings.getRandomSource());
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> registry, Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", (Supplier)(() -> {
            this.doCreateBiomes(registry, blender, structureFeatureManager, chunkAccess);
            return chunkAccess;
        })), Util.backgroundExecutor());
    }

    private void doCreateBiomes(Registry<Biome> registry, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(this.sampler, () -> new Beardifier(structureFeatureManager, chunkAccess), this.settings.get(), this.globalFluidPicker, blender);
        BiomeResolver biomeResolver = BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(this.runtimeBiomeSource), registry, chunkAccess);
        chunkAccess.fillBiomesFromNoise(biomeResolver, (i, j, k) -> this.sampler.target(i, j, k, noiseChunk.noiseData(i, k)));
    }

    @Override
    public Climate.Sampler climateSampler() {
        return this.sampler;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long l, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {}

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long l) {
        return new BzChunkGenerator(this.noises, this.biomeSource.withSeed(l), l, this.settings);
    }

    public boolean stable(long l, ResourceKey<NoiseGeneratorSettings> resourceKey) {
        return this.seed == l && this.settings.get().stable(resourceKey);
    }

    @Override
    public int getBaseHeight(int i, int j, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor) {
        NoiseSettings noiseSettings = this.settings.get().noiseSettings();
        int k = Math.max(noiseSettings.minY(), levelHeightAccessor.getMinBuildHeight());
        int l = Math.min(noiseSettings.minY() + noiseSettings.height(), levelHeightAccessor.getMaxBuildHeight());
        int m = Mth.intFloorDiv(k, noiseSettings.getCellHeight());
        int n = Mth.intFloorDiv(l - k, noiseSettings.getCellHeight());
        return n <= 0 ? levelHeightAccessor.getMinBuildHeight() : this.iterateNoiseColumn(i, j, null, types.isOpaque(), m, n).orElse(levelHeightAccessor.getMinBuildHeight());
    }

    @Override
    public NoiseColumn getBaseColumn(int i, int j, LevelHeightAccessor levelHeightAccessor) {
        NoiseSettings noiseSettings = this.settings.get().noiseSettings();
        int k = Math.max(noiseSettings.minY(), levelHeightAccessor.getMinBuildHeight());
        int l = Math.min(noiseSettings.minY() + noiseSettings.height(), levelHeightAccessor.getMaxBuildHeight());
        int m = Mth.intFloorDiv(k, noiseSettings.getCellHeight());
        int n = Mth.intFloorDiv(l - k, noiseSettings.getCellHeight());
        if (n <= 0) {
            return new NoiseColumn(k, EMPTY_COLUMN);
        } else {
            BlockState[] blockStates = new BlockState[n * noiseSettings.getCellHeight()];
            this.iterateNoiseColumn(i, j, blockStates, null, m, n);
            return new NoiseColumn(k, blockStates);
        }
    }

    private OptionalInt iterateNoiseColumn(int i, int j, @Nullable BlockState[] blockStates, @Nullable Predicate<BlockState> predicate, int k, int l) {
        NoiseSettings noiseSettings = this.settings.get().noiseSettings();
        int m = noiseSettings.getCellWidth();
        int n = noiseSettings.getCellHeight();
        int o = Math.floorDiv(i, m);
        int p = Math.floorDiv(j, m);
        int q = Math.floorMod(i, m);
        int r = Math.floorMod(j, m);
        int s = o * m;
        int t = p * m;
        double d = (double)q / (double)m;
        double e = (double)r / (double)m;
        NoiseChunk noiseChunk = NoiseChunk.forColumn(s, t, k, l, this.sampler, this.settings.get(), this.globalFluidPicker);
        noiseChunk.initializeForFirstCellX();
        noiseChunk.advanceCellX(0);

        for(int u = l - 1; u >= 0; --u) {
            noiseChunk.selectCellYZ(u, 0);

            for(int v = n - 1; v >= 0; --v) {
                int w = (k + u) * n + v;
                double f = (double)v / (double)n;
                noiseChunk.updateForY(f);
                noiseChunk.updateForX(d);
                noiseChunk.updateForZ(e);
                BlockState blockState = this.materialRule.apply(noiseChunk, i, w, j);
                BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
                if (blockStates != null) {
                    int x = u * n + v;
                    blockStates[x] = blockState2;
                }

                if (predicate != null && predicate.test(blockState2)) {
                    return OptionalInt.of(w + 1);
                }
            }
        }

        return OptionalInt.empty();
    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        if (!SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
            WorldGenerationContext worldGenerationContext = new WorldGenerationContext(this, worldGenRegion);
            NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
            NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(this.sampler, () -> new Beardifier(structureFeatureManager, chunkAccess), noiseGeneratorSettings, this.globalFluidPicker, Blender.of(worldGenRegion));
            this.surfaceSystem.buildSurface(worldGenRegion.getBiomeManager(), worldGenRegion.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), noiseGeneratorSettings.useLegacyRandomSource(), worldGenerationContext, chunkAccess, noiseChunk, noiseGeneratorSettings.surfaceRule());
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        NoiseSettings noiseSettings = this.settings.get().noiseSettings();
        LevelHeightAccessor levelHeightAccessor = chunkAccess.getHeightAccessorForGeneration();
        int i = Math.max(noiseSettings.minY(), levelHeightAccessor.getMinBuildHeight());
        int j = Math.min(noiseSettings.minY() + noiseSettings.height(), levelHeightAccessor.getMaxBuildHeight());
        int k = Mth.intFloorDiv(i, noiseSettings.getCellHeight());
        int l = Mth.intFloorDiv(j - i, noiseSettings.getCellHeight());
        if (l <= 0) {
            return CompletableFuture.completedFuture(chunkAccess);
        } else {
            int m = chunkAccess.getSectionIndex(l * noiseSettings.getCellHeight() - 1 + i);
            int n = chunkAccess.getSectionIndex(i);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for(int o = m; o >= n; --o) {
                LevelChunkSection levelChunkSection = chunkAccess.getSection(o);
                levelChunkSection.acquire();
                set.add(levelChunkSection);
            }

            return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", (Supplier)(() -> this.doFill(blender, structureFeatureManager, chunkAccess, k, l))), Util.backgroundExecutor()).whenCompleteAsync((chunkAccessx, throwable) -> {
                for(LevelChunkSection levelChunkSectionx : set) {
                    levelChunkSectionx.release();
                }

            }, executor);
        }
    }

    private ChunkAccess doFill(Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, int i, int j) {
        NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(this.sampler, () -> new Beardifier(structureFeatureManager, chunkAccess), noiseGeneratorSettings, this.globalFluidPicker, blender);
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunkAccess.getPos();
        int k = chunkPos.getMinBlockX();
        int l = chunkPos.getMinBlockZ();
        Aquifer aquifer = noiseChunk.aquifer();
        noiseChunk.initializeForFirstCellX();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        NoiseSettings noiseSettings = noiseGeneratorSettings.noiseSettings();
        int m = noiseSettings.getCellWidth();
        int n = noiseSettings.getCellHeight();
        int o = 16 / m;
        int p = 16 / m;

        for(int q = 0; q < o; ++q) {
            noiseChunk.advanceCellX(q);

            for(int r = 0; r < p; ++r) {
                LevelChunkSection levelChunkSection = chunkAccess.getSection(chunkAccess.getSectionsCount() - 1);

                for(int s = j - 1; s >= 0; --s) {
                    noiseChunk.selectCellYZ(s, r);

                    for(int t = n - 1; t >= 0; --t) {
                        int u = (i + s) * n + t;
                        int v = u & 15;
                        int w = chunkAccess.getSectionIndex(u);
                        if (chunkAccess.getSectionIndex(levelChunkSection.bottomBlockY()) != w) {
                            levelChunkSection = chunkAccess.getSection(w);
                        }

                        double d = (double)t / (double)n;
                        noiseChunk.updateForY(d);

                        for(int x = 0; x < m; ++x) {
                            int y = k + q * m + x;
                            int z = y & 15;
                            double e = (double)x / (double)m;
                            noiseChunk.updateForX(e);

                            for(int aa = 0; aa < m; ++aa) {
                                int ab = l + r * m + aa;
                                int ac = ab & 15;
                                double f = (double)aa / (double)m;
                                noiseChunk.updateForZ(f);
                                BlockState blockState = this.materialRule.apply(noiseChunk, y, u, ab);
                                if (blockState == null) {
                                    blockState = this.defaultBlock;
                                }

                                if (blockState != AIR && !SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
                                    if (blockState.getLightEmission() != 0 && chunkAccess instanceof ProtoChunk) {
                                        mutableBlockPos.set(y, u, ab);
                                        ((ProtoChunk)chunkAccess).addLight(mutableBlockPos);
                                    }

                                    levelChunkSection.setBlockState(z, v, ac, blockState, false);
                                    heightmap.update(z, u, ac, blockState);
                                    heightmap2.update(z, u, ac, blockState);
                                    if (aquifer.shouldScheduleFluidUpdate() && !blockState.getFluidState().isEmpty()) {
                                        mutableBlockPos.set(y, u, ab);
                                        chunkAccess.markPosForPostprocessing(mutableBlockPos);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            noiseChunk.swapSlices();
        }

        return chunkAccess;
    }

    @Override
    public int getGenDepth() {
        return this.settings.get().noiseSettings().height();
    }

    @Override
    public int getSeaLevel() {
        return this.settings.get().seaLevel();
    }

    @Override
    public int getMinY() {
        return this.settings.get().noiseSettings().minY();
    }

    /** @deprecated */
    @Deprecated
    public Optional<BlockState> topMaterial(CarvingContext carvingContext, Function<BlockPos, Biome> function, ChunkAccess chunkAccess, NoiseChunk noiseChunk, BlockPos blockPos, boolean bl) {
        return this.surfaceSystem.topMaterial(this.settings.get().surfaceRule(), carvingContext, function, chunkAccess, noiseChunk, blockPos, bl);
    }

    /**
     * For spawning specific mobs in certain places like structures.
     */
    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager accessor, MobCategory group, BlockPos pos) {
        return super.getMobsAt(biome, accessor, group, pos);
    }

    /**
     * Dedicated to spawning slimes/bees when generating chunks initially.
     * This is so there's lots of bees and the slime can spawn despite the
     * slime having extremely restrictive spawning mechanics.
     * <p>
     * Also spawns bees with chance to bee full of pollen
     * <p>
     * This is mainly vanilla code but with biome$spawnlistentry changed to
     * use bee/slime and the restrictive terrain check called on the entity removed.
     * The height is also restricted so the mob cannot spawn on the ceiling of this
     * dimension as well.
     */
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        NoiseGeneratorSettings noiseGeneratorSettings = this.settings.get();
        if (!((NoiseGeneratorSettingsInvoker)(Object)noiseGeneratorSettings).thebumblezone_callDisableMobGeneration()) {
            ChunkPos chunkPos = region.getCenter();
            Biome biome = region.getBiome(chunkPos.getWorldPosition());
            WorldgenRandom sharedseedrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
            sharedseedrandom.setDecorationSeed(region.getSeed(), chunkPos.getMinBlockX(), chunkPos.getMinBlockZ());
            while (sharedseedrandom.nextFloat() < biome.getMobSettings().getCreatureProbability() * 0.75f) {
                //20% of time, spawn honey slime. Otherwise, spawn bees.
                MobSpawnSettings.SpawnerData biome$spawnlistentry;
                float threshold = sharedseedrandom.nextFloat();
                if(threshold < 0.25f) {
                    biome$spawnlistentry = INITIAL_HONEY_SLIME_ENTRY;
                }
                else if (threshold < 0.95f) {
                    biome$spawnlistentry = INITIAL_BEE_ENTRY;
                }
                else {
                    biome$spawnlistentry = INITIAL_BEEHEMOTH_ENTRY;
                }

                int startingX = chunkPos.getMinBlockX() + sharedseedrandom.nextInt(16);
                int startingZ = chunkPos.getMinBlockZ() + sharedseedrandom.nextInt(16);

                BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(startingX, 0, startingZ);
                int height = BzPlacingUtils.topOfSurfaceBelowHeight(region, sharedseedrandom.nextInt(255), -1, blockpos) + 1;

                if (biome$spawnlistentry.type.canSummon() && height > 0 && height < 255) {
                    float width = biome$spawnlistentry.type.getWidth();
                    double xLength = Mth.clamp(startingX, (double) chunkPos.getMinBlockX() + (double) width, (double) chunkPos.getMinBlockX() + 16.0D - (double) width);
                    double zLength = Mth.clamp(startingZ, (double) chunkPos.getMinBlockZ() + (double) width, (double) chunkPos.getMinBlockZ() + 16.0D - (double) width);

                    Entity entity = biome$spawnlistentry.type.create(region.getLevel());
                    if(entity == null)
                        continue;

                    entity.moveTo(xLength, height, zLength, sharedseedrandom.nextFloat() * 360.0F, 0.0F);
                    if (entity instanceof Mob mobEntity) {
                        if (mobEntity.checkSpawnRules(region, MobSpawnType.CHUNK_GENERATION) && mobEntity.checkSpawnObstruction(region)) {
                            mobEntity.finalizeSpawn(region, region.getCurrentDifficultyAt(new BlockPos(mobEntity.position())), MobSpawnType.CHUNK_GENERATION, null, null);
                            region.addFreshEntity(mobEntity);
                        }
                    }
                }
            }
        }
    }
}
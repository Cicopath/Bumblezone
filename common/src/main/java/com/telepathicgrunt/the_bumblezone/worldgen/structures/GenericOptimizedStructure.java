package com.telepathicgrunt.the_bumblezone.worldgen.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.the_bumblezone.modinit.BzStructures;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class GenericOptimizedStructure extends Structure {

    public static final Codec<GenericOptimizedStructure> CODEC = RecordCodecBuilder.<GenericOptimizedStructure>mapCodec(instance ->
            instance.group(GenericOptimizedStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.intRange(1, 100).optionalFieldOf("valid_biome_radius_check").forGetter(structure -> structure.biomeRadius),
                    Codec.intRange(1, 100).optionalFieldOf("min_y_limit").forGetter(structure -> structure.minYLimit),
                    Codec.BOOL.fieldOf("disable_bound_checks").orElse(false).forGetter(structure -> structure.disableBoundChecks)
            ).apply(instance, GenericOptimizedStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    public final Optional<Integer> biomeRadius;
    public final Optional<Integer> minYLimit;
    public final boolean disableBoundChecks;

    public GenericOptimizedStructure(StructureSettings config,
                                     Holder<StructureTemplatePool> startPool,
                                     Optional<ResourceLocation> startJigsawName,
                                     int size,
                                     HeightProvider startHeight,
                                     Optional<Heightmap.Types> projectStartToHeightmap,
                                     int maxDistanceFromCenter,
                                     Optional<Integer> biomeRadius,
                                     Optional<Integer> minYLimit,
                                     boolean disableBoundChecks)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.biomeRadius = biomeRadius;
        this.minYLimit = minYLimit;
        this.disableBoundChecks = disableBoundChecks;
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int y = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos centerPos = new BlockPos(chunkpos.getMinBlockX(), y, chunkpos.getMinBlockZ());

        if (this.biomeRadius.isPresent() && !(context.biomeSource() instanceof CheckerboardColumnBiomeSource)) {
            int validBiomeRange = this.biomeRadius.get();
            int sectionY = centerPos.getY();
            if (projectStartToHeightmap.isPresent()) {
                sectionY += GeneralUtils.getLowestLand(
                        context.chunkGenerator(),
                        context.randomState(),
                        centerPos,
                        context.heightAccessor(),
                        true,
                        projectStartToHeightmap.get() == Heightmap.Types.OCEAN_FLOOR_WG
                ).getY();
            }
            sectionY = QuartPos.fromBlock(sectionY);

            for (int curChunkX = chunkpos.x - validBiomeRange; curChunkX <= chunkpos.x + validBiomeRange; curChunkX++) {
                for (int curChunkZ = chunkpos.z - validBiomeRange; curChunkZ <= chunkpos.z + validBiomeRange; curChunkZ++) {
                    Holder<Biome> biome = context.biomeSource().getNoiseBiome(QuartPos.fromSection(curChunkX), sectionY, QuartPos.fromSection(curChunkZ), context.randomState().sampler());
                    if (!context.validBiome().test(biome)) {
                        return Optional.empty();
                    }
                }
            }
        }

        return OptimizedJigsawManager.assembleJigsawStructure(
                context,
                this.startPool,
                this.size,
                context.registryAccess().registry(Registries.STRUCTURE).get().getKey(this),
                centerPos,
                false,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter,
                minYLimit,
                (structurePiecesBuilder, pieces) -> GeneralUtils.centerAllPieces(centerPos, pieces),
                this.disableBoundChecks);
    }

    @Override
    public StructureType<?> type() {
        return BzStructures.GENERIC_OPTIMIZED_STRUCTURE.get();
    }
}
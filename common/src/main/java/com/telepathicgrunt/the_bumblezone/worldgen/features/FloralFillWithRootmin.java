package com.telepathicgrunt.the_bumblezone.worldgen.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.the_bumblezone.entities.mobs.RootminEntity;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import com.telepathicgrunt.the_bumblezone.utils.UnsafeBulkSectionAccess;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.FloralFillWithRootminConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;


public class FloralFillWithRootmin extends Feature<FloralFillWithRootminConfig> {

    public FloralFillWithRootmin(Codec<FloralFillWithRootminConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean place(FeaturePlaceContext<FloralFillWithRootminConfig> context) {
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        FloralFillWithRootminConfig config = context.config();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos chunkCornerPos = new ChunkPos(context.origin()).getWorldPosition().above(context.origin().getY());

        Optional<HolderSet.Named<Block>> optionalBlocks = BuiltInRegistries.BLOCK.getTag(config.flowerTag);
        List<Block> blockList = GeneralUtils.convertHoldersetToList(optionalBlocks);
        blockList.removeIf(block -> block.defaultBlockState().is(context.config().disallowedFlowerTag));

        if (blockList.isEmpty()) {
            return false;
        }

        ChunkAccess cachedChunk = level.getChunk(chunkCornerPos);
        UnsafeBulkSectionAccess bulkSectionAccess = new UnsafeBulkSectionAccess(context.level());
        for (int xOffset = 0; xOffset < 16; xOffset++) {
            for (int zOffset = 0; zOffset < 16; zOffset++) {
                boolean spawnRootmin = false;
                boolean spawnFlower = false;
                if (randomSource.nextFloat() < config.rootminChance) {
                    spawnRootmin = true;
                }
                else if (randomSource.nextFloat() < config.flowerChance) {
                    spawnFlower = true;
                }

                if (!spawnRootmin && !spawnFlower) {
                    continue;
                }

                mutable.set(chunkCornerPos).move(xOffset, 0, zOffset);
                boolean isAtGrassBlock = setMutableToGrassBlock(cachedChunk, bulkSectionAccess, mutable);
                if (!isAtGrassBlock || mutable.getY() <= cachedChunk.getMinBuildHeight() || mutable.getY() >= cachedChunk.getMaxBuildHeight()) {
                    continue;
                }

                if (!bulkSectionAccess.getBlockState(mutable.above()).isAir()) {
                    continue;
                }

                BlockState chosenFlower = blockList.get(randomSource.nextInt(blockList.size())).defaultBlockState();
                if (chosenFlower.hasProperty(BlockStateProperties.FLOWER_AMOUNT)) {
                    chosenFlower = chosenFlower.setValue(BlockStateProperties.FLOWER_AMOUNT, 4);
                }

                if (chosenFlower.getBlock() instanceof DoublePlantBlock) {
                    chosenFlower = chosenFlower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
                }

                if (spawnRootmin && mutable.getY() != 39) {
                    bulkSectionAccess.setBlockState(mutable, Blocks.AIR.defaultBlockState(), false);

                    Entity spawningEntity = BzEntities.ROOTMIN.get().create(level.getLevel());
                    if (spawningEntity instanceof RootminEntity rootmin) {
                        Optional<HolderSet.Named<Block>> optionalRootminBlocks = BuiltInRegistries.BLOCK.getTag(config.rootminFlowerTag);
                        List<Block> rootminBlockList = GeneralUtils.convertHoldersetToList(optionalRootminBlocks);
                        rootminBlockList.removeIf(block -> block.defaultBlockState().is(context.config().disallowedRootminFlowerTag));
                        BlockState chosenRootminFlower;

                        if (rootminBlockList.isEmpty()) {
                            chosenRootminFlower = Blocks.AIR.defaultBlockState();
                        }
                        else {
                            chosenRootminFlower = rootminBlockList.get(randomSource.nextInt(rootminBlockList.size())).defaultBlockState();
                        }

                        rootmin.finalizeSpawn(
                                level,
                                level.getCurrentDifficultyAt(mutable),
                                MobSpawnType.CHUNK_GENERATION,
                                null,
                                null);

                        rootmin.setPersistenceRequired();
                        rootmin.setFlowerBlock(chosenRootminFlower);
                        rootmin.moveTo(
                                (double)mutable.getX() + 0.5D,
                                mutable.getY(),
                                (double)mutable.getZ() + 0.5D,
                                0.0F,
                                0.0F);
                        rootmin.hideAsBlock(new Vec3(
                                mutable.getX() + 0.5D,
                                mutable.getY(),
                                mutable.getZ() + 0.5D));
                        rootmin.yHeadRot = 0;
                        rootmin.yHeadRotO = 0;
                        rootmin.yBodyRot = 0;
                        rootmin.yBodyRotO = 0;

                        level.addFreshEntityWithPassengers(rootmin);

                        mutable.move(Direction.UP);
                        BlockState aboveState = bulkSectionAccess.getBlockState(mutable);
                        if (aboveState.is(BlockTags.REPLACEABLE)) {
                            bulkSectionAccess.setBlockState(mutable, Blocks.AIR.defaultBlockState(), false);

                            if (aboveState.getBlock() instanceof DoublePlantBlock){
                                mutable.move(Direction.UP);
                                bulkSectionAccess.setBlockState(mutable, Blocks.AIR.defaultBlockState(), false);
                            }
                        }
                    }
                }
                else {
                    if (chosenFlower.getBlock() instanceof DoublePlantBlock) {
                        mutable.move(Direction.UP, 2);
                        BlockState aboveState = bulkSectionAccess.getBlockState(mutable);

                        if (aboveState.isAir() || aboveState.is(BlockTags.REPLACEABLE)) {
                            mutable.move(Direction.DOWN);
                            bulkSectionAccess.setBlockState(mutable, chosenFlower, false);

                            chosenFlower = chosenFlower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                            mutable.move(Direction.UP);
                            bulkSectionAccess.setBlockState(mutable, chosenFlower, false);
                        }
                    }
                    else {
                        mutable.move(Direction.UP);
                        bulkSectionAccess.setBlockState(mutable, chosenFlower, false);
                    }
                }
            }
        }
        return true;
    }

    private boolean setMutableToGrassBlock(ChunkAccess cachedChunk, UnsafeBulkSectionAccess bulkSectionAccess, BlockPos.MutableBlockPos mutable) {
        BlockState currentState = bulkSectionAccess.getBlockState(mutable);
        Direction previousDirection = null;
        while (!currentState.is(Blocks.GRASS_BLOCK) &&
                mutable.getY() > cachedChunk.getMinBuildHeight() &&
                mutable.getY() < cachedChunk.getMaxBuildHeight())
        {
            if (currentState.is(Blocks.CAVE_AIR)) {
                if (previousDirection == Direction.DOWN) {
                    return false;
                }

                mutable.move(Direction.UP);
                previousDirection = Direction.UP;
            }
            else if (currentState.isAir() || !currentState.getFluidState().isEmpty()) {
                if (previousDirection == Direction.UP) {
                    return false;
                }

                mutable.move(Direction.DOWN);
                previousDirection = Direction.DOWN;
            }
            else {
                if (previousDirection == Direction.DOWN) {
                    return false;
                }

                mutable.move(Direction.UP);
                previousDirection = Direction.UP;
            }

            currentState = bulkSectionAccess.getBlockState(mutable);
        }

        return currentState.is(Blocks.GRASS_BLOCK);
    }
}
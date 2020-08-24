package net.telepathicgrunt.bumblezone.surfacebuilders;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.telepathicgrunt.bumblezone.blocks.BzBlocks;

import java.util.Random;


public class HoneySurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public HoneySurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    private static final BlockState STONE = Blocks.STONE.getDefaultState();
    private static final BlockState FILLED_POROUS_HONEYCOMB = BzBlocks.FILLED_POROUS_HONEYCOMB.getDefaultState();
    private static final BlockState POROUS_HONEYCOMB = BzBlocks.POROUS_HONEYCOMB.getDefaultState();
    private static final BlockState HONEYCOMB_BLOCK = Blocks.HONEYCOMB_BLOCK.getDefaultState();


    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TernarySurfaceConfig config) {
        //creates grass surface normally
        SurfaceBuilder.DEFAULT.generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);

        int xpos = x & 15;
        int zpos = z & 15;
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();

        //makes stone below sea level into end stone
        for (int ypos = 255; ypos >= 0; --ypos) {
            blockpos$Mutable.set(xpos, ypos, zpos);
            BlockState currentBlockState = chunkIn.getBlockState(blockpos$Mutable);

            if (currentBlockState.getBlock() != null && currentBlockState.getMaterial() != Material.AIR) {
                if (currentBlockState == STONE) {
                    chunkIn.setBlockState(blockpos$Mutable, HONEYCOMB_BLOCK, false);
                } else if (currentBlockState == POROUS_HONEYCOMB) {
                    if (ypos <= 40 + 2 + Math.max(noise, 0) + random.nextInt(2)) {
                        chunkIn.setBlockState(blockpos$Mutable, FILLED_POROUS_HONEYCOMB, false);
                    }
                } else if (currentBlockState.isAir()) {
                    if (ypos < 40) {
                        chunkIn.setBlockState(blockpos$Mutable, defaultFluid, false);
                    }
                }
            }
        }

    }
}
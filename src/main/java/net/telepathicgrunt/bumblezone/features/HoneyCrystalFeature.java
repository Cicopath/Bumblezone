package net.telepathicgrunt.bumblezone.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.telepathicgrunt.bumblezone.blocks.BzBlocks;
import net.telepathicgrunt.bumblezone.blocks.HoneyCrystal;

public class HoneyCrystalFeature extends Feature<NoFeatureConfig> {

    public HoneyCrystalFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory) {
	super(configFactory);
    }

    private static final Block CAVE_AIR = Blocks.CAVE_AIR;
    private static final Block AIR = Blocks.AIR;

    /**
     * Place crystal block attached to a block if it is buried underground or underwater
     */
    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> changedBlock, Random rand, BlockPos position, NoFeatureConfig config) {

	BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable(position);
	BlockState originalBlockstate = world.getBlockState(blockpos$Mutable);
	BlockState blockstate;
	
	if (originalBlockstate.getBlock() == CAVE_AIR || originalBlockstate.getFluidState().isTagged(FluidTags.WATER)) {

	    for (Direction face : Direction.values()) {
		blockpos$Mutable.setPos(position);
		blockstate = world.getBlockState(blockpos$Mutable.move(face, 7));
		
		if (blockstate.getBlock() == AIR) {
		    return false; // too close to the outside. Refuse generation
		}
	    }
	    
	    
	    BlockState honeyCrystal = BzBlocks.HONEY_CRYSTAL.get().getDefaultState()
		    			.with(HoneyCrystal.WATERLOGGED, originalBlockstate.getFluidState().isTagged(FluidTags.WATER));

	    //loop through all 6 directions
	    blockpos$Mutable.setPos(position);
	    for(Direction facing : Direction.values()) {
		
		honeyCrystal = honeyCrystal.with(HoneyCrystal.FACING, facing);
		
		// if the block is solid, place crystal on it
		if (honeyCrystal.isValidPosition(world, blockpos$Mutable)) {
		   
		    //if the spot is invalid, we get air back
		    BlockState result = HoneyCrystal.getValidBlockForPosition(honeyCrystal, world, blockpos$Mutable);
		    if(result.getBlock() != AIR)
		    {
			world.setBlockState(blockpos$Mutable, result, 3);
			return true; //crystal was placed
		    }
		}
	    }
	}

	return false;
    }

}
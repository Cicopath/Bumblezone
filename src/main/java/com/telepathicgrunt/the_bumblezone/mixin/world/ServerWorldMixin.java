package com.telepathicgrunt.the_bumblezone.mixin.world;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.BeeDedicatedSpawning;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {
	protected ServerWorldMixin(WritableLevelData writableLevelData, ResourceKey<Level> levelResourceKey, Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> fillerSupplier, boolean b, boolean b1, long l) {
		super(writableLevelData, levelResourceKey, dimensionType, fillerSupplier, b, b1, l);
	}

	@Unique
	private static final int thebumblezone_updateInterval = 20;

	@Unique
	private static int thebumblezone_counter = 0;

	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(value = "HEAD")
	)
	private void thebumblezone_countBzDimEntities(CallbackInfo ci) {
		if(dimension().location().equals(Bumblezone.MOD_DIMENSION_ID)){
			thebumblezone_counter++;
			if(thebumblezone_counter % thebumblezone_updateInterval == 0){
				thebumblezone_counter = 0;
				GeneralUtils.updateEntityCount(((ServerLevel) (Object) this));
				if(Bumblezone.BZ_CONFIG.BZGeneralConfig.specialBeeSpawning) {
					BeeDedicatedSpawning.specialSpawnBees((ServerLevel) (Object) this);
				}
			}
		}
	}
}

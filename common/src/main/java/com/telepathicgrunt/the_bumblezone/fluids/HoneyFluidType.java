package com.telepathicgrunt.the_bumblezone.fluids;

import com.teamresourceful.resourcefullib.common.fluid.data.FluidProperties;
import com.telepathicgrunt.the_bumblezone.client.rendering.fluids.HoneyFluidClientProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.PathType;

public class HoneyFluidType {

    public static FluidProperties.Builder create() {

        return FluidProperties.builder()
                .supportsBoating(true)
                .canHydrate(false)
                .canDrown(true)
                .canExtinguish(true)
                .canPushEntity(true)
                .canSwim(true)
                .pathType(PathType.WATER)
                .adjacentPathType(PathType.WATER_BORDER)
                .canConvertToSource(false)
                .fallDistanceModifier(0.15f)
                .motionScale(0.0115)
                .rarity(Rarity.UNCOMMON)
                .viscosity(5000)
                .density(2000)
                .temperature(300)
                .tickRate(30)
                .still(HoneyFluidClientProperties.HONEY_FLUID_STILL_TEXTURE)
                .flowing(HoneyFluidClientProperties.HONEY_FLUID_FLOWING_TEXTURE)
                .overlay(HoneyFluidClientProperties.HONEY_FLUID_FLOWING_TEXTURE)
                .sounds("bucket_fill", SoundEvents.BUCKET_FILL)
                .sounds("bucket_empty", SoundEvents.BUCKET_EMPTY)
                .sounds("fluid_vaporize", SoundEvents.FIRE_EXTINGUISH);
    }
}

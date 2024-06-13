package com.telepathicgrunt.the_bumblezone.worldgen.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FloralFillWithRootminConfig implements FeatureConfiguration {
    public static final Codec<FloralFillWithRootminConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.floatRange(0, 1).fieldOf("rootmin_chance").orElse(0F).forGetter((config) -> config.rootminChance),
            Codec.floatRange(0, 1).fieldOf("flower_chance").orElse(0.5F).forGetter((config) -> config.flowerChance),
            TagKey.codec(Registries.BLOCK).fieldOf("flower_tag").forGetter(config -> config.flowerTag),
            TagKey.codec(Registries.BLOCK).fieldOf("disallowed_flower_tag").forGetter(config -> config.disallowedFlowerTag),
            TagKey.codec(Registries.BLOCK).fieldOf("rootmin_flower_tag").forGetter(config -> config.rootminFlowerTag),
            TagKey.codec(Registries.BLOCK).fieldOf("disallowed_rootmin_flower_tag").forGetter(config -> config.disallowedRootminFlowerTag)
        ).apply(instance, FloralFillWithRootminConfig::new));

    public final float rootminChance;
    public final float flowerChance;
    public final TagKey<Block> flowerTag;
    public final TagKey<Block> disallowedFlowerTag;
    public final TagKey<Block> rootminFlowerTag;
    public final TagKey<Block> disallowedRootminFlowerTag;

    public FloralFillWithRootminConfig(
            float rootminChance,
            float flowerChance,
            TagKey<Block> flowerTag,
            TagKey<Block> disallowedFlowerTag,
            TagKey<Block> rootminFlowerTag,
            TagKey<Block> disallowedRootminFlowerTag)
    {
        this.rootminChance = rootminChance;
        this.flowerChance = flowerChance;
        this.flowerTag = flowerTag;
        this.disallowedFlowerTag = disallowedFlowerTag;
        this.rootminFlowerTag = rootminFlowerTag;
        this.disallowedRootminFlowerTag = disallowedRootminFlowerTag;
    }
}

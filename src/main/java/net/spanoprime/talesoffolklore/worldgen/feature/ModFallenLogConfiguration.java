package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

// Excerpt da Minecraft 1.21
public record ModFallenLogConfiguration(
        BlockStateProvider logProvider,
        UniformInt length,
        UniformInt stumpHeight,
        float scale,
        WeightedStateProvider decoratorProvider
) implements FeatureConfiguration {
    public static final Codec<ModFallenLogConfiguration> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BlockStateProvider.CODEC.fieldOf("log_provider").forGetter(ModFallenLogConfiguration::logProvider),
                    UniformInt.CODEC.fieldOf("length").forGetter(ModFallenLogConfiguration::length),
                    UniformInt.CODEC.fieldOf("stump_height").forGetter(ModFallenLogConfiguration::stumpHeight),
                    Codec.FLOAT.fieldOf("scale").forGetter(ModFallenLogConfiguration::scale),
                    WeightedStateProvider.CODEC.fieldOf("decorator_provider").forGetter(ModFallenLogConfiguration::decoratorProvider)
            ).apply(inst, ModFallenLogConfiguration::new)
    );
}

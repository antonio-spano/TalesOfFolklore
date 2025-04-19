package net.spanoprime.talesoffolklore.worldgen.tree;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.spanoprime.talesoffolklore.worldgen.ModConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class VirginiaPineTreeGrower extends AbstractTreeGrower
{
    @Override
    protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
        return ModConfiguredFeatures.VIRGINIA_PINE_KEY;
    }
}

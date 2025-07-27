package net.spanoprime.talesoffolklore.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface MultiNoiseBiomeSourceAccessor {
    void setLastSampledSeed(long seed);
    void setLastSampledDimension(ResourceKey<Level> dimension);
    long getLastSampledSeed();
    ResourceKey<Level> getLastSampledDimension();
}

package net.spanoprime.talesoffolklore.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface MultiNoiseBiomeSourceAccessor {
    void setLastSampledSeed(long seed);
    void setLastSampledDimension(ResourceKey<Level> dimension);
    void setServerLevel(ServerLevel level);
    long getLastSampledSeed();
    ResourceKey<Level> getLastSampledDimension();
    ServerLevel getServerLevel();
}

package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
@Mixin(net.minecraft.world.level.levelgen.NoiseChunk.class)
public interface NoiseChunkAccessor {
    @Invoker("cachedClimateSampler")
    Climate.Sampler talesoffolklore_invokeCachedClimateSampler(
            NoiseRouter pNoiseRouter,
            List<Climate.ParameterPoint> pPoints
    );
}

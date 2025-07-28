package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.*;
import net.spanoprime.talesoffolklore.accessor.MultiNoiseBiomeSourceAccessor;
import net.spanoprime.talesoffolklore.util.VoronoiGenerator;
import net.spanoprime.talesoffolklore.worldgen.biome.ModLandFinder;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector; // Importa la cassaforte
import net.spanoprime.talesoffolklore.worldgen.noise.ModBiomeRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(value = MultiNoiseBiomeSource.class, priority = -69420)
public abstract class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccessor {

    @Unique
    private long talesoffolklore$lastSampledSeed = 0L;
    @Unique
    long talesoffolklore$lastCalculatedSeed = 0L;
    @Unique
    private ResourceKey<Level> talesoffolklore$lastSampledDimension = null;
    @Unique
    private ServerLevel talesoffolklore$serverLevel = null;

    @Unique
    private static final long talesoffolklore$RADIUS_SQ = (long) BiomeInjector.appalachianRadius * BiomeInjector.appalachianRadius;

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void talesoffolklore$forceBiomeInCircle(int xQuart, int yQuart, int zQuart, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        // Se la cassaforte è vuota, non fare nulla.
        if (BiomeInjector.APPALACHIAN_FOREST_HOLDER == null ) return;
        if (getServerLevel() == null) return;

        Holder<Biome> original = cir.getReturnValue();
        if (original == null) return;

        //VoronoiGenerator.VoronoiInfo voronoiInfo = ModBiomeRarity.getMythicBiomeInfo(talesoffolklore$lastSampledSeed, xQuart, zQuart);

        if (talesoffolklore$lastSampledSeed != talesoffolklore$lastCalculatedSeed)
        {
            talesoffolklore$lastCalculatedSeed = talesoffolklore$lastSampledSeed;
            BiomeInjector.appalachianCenter = BiomeInjector.findLandCenter(
                    yQuart,
                    BiomeInjector.minDistance,
                    BiomeInjector.maxDistanceOffset,
                    talesoffolklore$lastSampledSeed,
                    getServerLevel());
        }

        int blockX = xQuart << 2;
        int blockZ = zQuart << 2;
        long dx = (long) blockX - BiomeInjector.appalachianCenter.getX();
        long dz = (long) blockZ - BiomeInjector.appalachianCenter.getZ();

        if (dx * dx + dz * dz <= talesoffolklore$RADIUS_SQ /*&& !BiomeInjector.isAquaticBiome(original)*/)
        {
            cir.setReturnValue(BiomeInjector.APPALACHIAN_FOREST_HOLDER);
        }
        //cir.setReturnValue(cir.getReturnValue());
    }

    @Inject(
            method = "collectPossibleBiomes",
            at = @At("RETURN"),
            cancellable = true
    )
    private void talesoffolklore$addBiomeToOfficialList(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
        // Se la cassaforte è vuota, non fare nulla.
        if (BiomeInjector.APPALACHIAN_FOREST_HOLDER == null) return;

        Stream<Holder<Biome>> originalStream = cir.getReturnValue();
        Stream<Holder<Biome>> modifiedStream = Stream.concat(originalStream, Stream.of(BiomeInjector.APPALACHIAN_FOREST_HOLDER)).distinct();

        cir.setReturnValue(modifiedStream);
    }

    @Override
    public void setLastSampledSeed(long seed) {
        this.talesoffolklore$lastSampledSeed = seed;
    }

    @Override
    public void setLastSampledDimension(ResourceKey<Level> dimension) {
        this.talesoffolklore$lastSampledDimension = dimension;
    }

    @Override
    public void setServerLevel(ServerLevel level)
    {
        this.talesoffolklore$serverLevel = level;
    }

    @Override
    public long getLastSampledSeed() {
        return this.talesoffolklore$lastSampledSeed;
    }

    @Override
    public ResourceKey<Level> getLastSampledDimension() {
        return this.talesoffolklore$lastSampledDimension;
    }

    @Override
    public ServerLevel getServerLevel()
    {
        return this.talesoffolklore$serverLevel;
    }
}
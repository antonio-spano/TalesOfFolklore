package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.spanoprime.talesoffolklore.accessor.MultiNoiseBiomeSourceAccessor;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector;
import net.spanoprime.talesoffolklore.worldgen.biome.ModLandFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(value = MultiNoiseBiomeSource.class, priority = -69420)
public abstract class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccessor {

    @Unique private long talesoffolklore$lastSampledSeed    = 0L;
    @Unique private long talesoffolklore$lastCalculatedSeed = 0L;
    @Unique private ResourceKey<Level> talesoffolklore$lastSampledDimension = null;
    @Unique private ServerLevel talesoffolklore$serverLevel = null;
    @Unique private static final long R2 = (long)BiomeInjector.appalachianRadius * BiomeInjector.appalachianRadius;

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void talesoffolklore$forceBiomeInCircle(int xQuart, int yQuart, int zQuart, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (BiomeInjector.APPALACHIAN_FOREST_HOLDER == null || getServerLevel() == null) {
            return;
        }

        long currentSeed = getLastSampledSeed();
        if (currentSeed != talesoffolklore$lastCalculatedSeed) {
            talesoffolklore$lastCalculatedSeed = currentSeed;
            ServerLevel level = getServerLevel();

            BlockPos center;
                // calcola un candidato
            center = BiomeInjector.findLandCenter(
                    yQuart,
                    BiomeInjector.minDistance,
                    BiomeInjector.maxDistanceOffset,
                    currentSeed,
                    level
            );
                // verifica che cada su terreno emerso

            // tek: ora assegniamo finalmente l'offset valido
            BiomeInjector.appalachianCenter = center;
        }

        int blockX = xQuart << 2;
        int blockZ = zQuart << 2;
        long dx = (long) blockX - BiomeInjector.appalachianCenter.getX();
        long dz = (long) blockZ - BiomeInjector.appalachianCenter.getZ();

        if (dx*dx + dz*dz <= R2) {
            cir.setReturnValue(BiomeInjector.APPALACHIAN_FOREST_HOLDER);
        }
    }

    @Inject(
            method = "collectPossibleBiomes",
            at = @At("RETURN"),
            cancellable = true
    )
    private void talesoffolklore$addBiomeToOfficialList(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
        if (BiomeInjector.APPALACHIAN_FOREST_HOLDER == null) return;
        cir.setReturnValue(
                Stream.concat(cir.getReturnValue(), Stream.of(BiomeInjector.APPALACHIAN_FOREST_HOLDER))
                        .distinct()
        );
    }

    @Override public void setLastSampledSeed(long seed)                    { this.talesoffolklore$lastSampledSeed = seed; }
    @Override public void setLastSampledDimension(ResourceKey<Level> dim)   { this.talesoffolklore$lastSampledDimension = dim; }
    @Override public void setServerLevel(ServerLevel level)                { this.talesoffolklore$serverLevel = level; }
    @Override public long getLastSampledSeed()                             { return this.talesoffolklore$lastSampledSeed; }
    @Override public ResourceKey<Level> getLastSampledDimension()          { return this.talesoffolklore$lastSampledDimension; }
    @Override public ServerLevel getServerLevel()                          { return this.talesoffolklore$serverLevel; }
}

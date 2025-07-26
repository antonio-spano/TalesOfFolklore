package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Biome;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector; // Importa la cassaforte
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1001)
public abstract class MultiNoiseBiomeSourceMixin {
    @Unique
    private static final int talesoffolklore$CENTER_X = 4000;
    @Unique
    private static final int talesoffolklore$CENTER_Z = -4000;
    @Unique
    private static final int talesoffolklore$RADIUS   = 250;
    @Unique
    private static final long talesoffolklore$RADIUS_SQ = (long) talesoffolklore$RADIUS * talesoffolklore$RADIUS;

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void talesoffolklore$forceBiomeInCircle(int xQuart, int yQuart, int zQuart, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        // Se la cassaforte è vuota, non fare nulla.
        if (BiomeInjector.APPALACHIAN_FOREST_HOLDER == null) return;

        int blockX = xQuart << 2;
        int blockZ = zQuart << 2;
        long dx = (long) blockX - talesoffolklore$CENTER_X;
        long dz = (long) blockZ - talesoffolklore$CENTER_Z;

        if (dx * dx + dz * dz <= talesoffolklore$RADIUS_SQ) {
            cir.setReturnValue(BiomeInjector.APPALACHIAN_FOREST_HOLDER);
        }
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
}
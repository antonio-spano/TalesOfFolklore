package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

import java.util.List;

// ModNoiseParameters.java  (datagen o init statico)
public final class ModNoiseParameters {
    public static final ResourceKey<NormalNoise.NoiseParameters> VARIANT_NOISE =
            ResourceKey.create(Registries.NOISE, new ResourceLocation(TalesOfFolklore.MOD_ID, "variant_noise"));

    public static void bootstrap(BootstapContext<NormalNoise.NoiseParameters> ctx) {
        // frequenza alta: firstOctave = 0, amplitude ~ 1
        ctx.register(VARIANT_NOISE,
                new NormalNoise.NoiseParameters(/* firstOctave */ 0,
                        /* amplitudes  */ List.of(1.0D, 1.0D, 1.0D)));
    }
}

package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class WrapperBiomeSource extends BiomeSource {

    // IL CODEC (Era già giusto)
    public static final Codec<WrapperBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("fallback_source").forGetter(WrapperBiomeSource::getFallbackSource),
                    Codec.INT.fieldOf("center_x").forGetter(WrapperBiomeSource::getCenterX),
                    Codec.INT.fieldOf("center_z").forGetter(WrapperBiomeSource::getCenterZ),
                    Codec.LONG.fieldOf("radius_sq").forGetter(WrapperBiomeSource::getRadiusSq),
                    Biome.CODEC.fieldOf("biome").forGetter(WrapperBiomeSource::getAppalachianBiome)
            ).apply(instance, WrapperBiomeSource::new)
    );

    // I CAMPI (Erano già giusti)
    private final BiomeSource fallbackSource;
    private final int centerX;
    private final int centerZ;
    private final long radiusSq;
    private final Holder<Biome> appalachianBiome;

    // IL COSTRUTTORE (Corretto con super() vuoto)
    public WrapperBiomeSource(BiomeSource fallbackSource, int centerX, int centerZ, long radiusSq, Holder<Biome> appalachianBiome) {
        super(); // Il costruttore padre non vuole argomenti.
        this.fallbackSource = fallbackSource;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radiusSq = radiusSq;
        this.appalachianBiome = appalachianBiome;
    }

    // QUESTO È IL METODO MANCANTE CHE CAUSAVA TUTTI I PROBLEMI.
    // Deve dire al gioco quali biomi sono possibili in questo mondo.
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        // La risposta è: tutti i biomi di vanilla (dal nostro fallback) PIÙ il nostro bioma custom.
        return Stream.concat(this.fallbackSource.possibleBiomes().stream(), Stream.of(this.appalachianBiome));
    }

    // QUESTO ERA GIÀ GIUSTO
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        long distSq = (long)(x - centerX) * (x - centerX) + (long)(z - centerZ) * (z - centerZ);
        return distSq <= this.radiusSq ? this.appalachianBiome : this.fallbackSource.getNoiseBiome(x, y, z, sampler);
    }

    // QUESTO ERA GIÀ GIUSTO
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    // I GETTER (Erano già giusti)
    public BiomeSource getFallbackSource() {
        return fallbackSource;
    }
    public int getCenterX() {
        return centerX;
    }
    public int getCenterZ() {
        return centerZ;
    }
    public long getRadiusSq() {
        return radiusSq;
    }
    public Holder<Biome> getAppalachianBiome() {
        return appalachianBiome;
    }
}
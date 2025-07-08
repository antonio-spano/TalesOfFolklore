package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class WrapperBiomeSource extends BiomeSource {

    // IL CODEC CORRETTO. USA I GETTER INVECE DEI LAMBDA.
    public static final Codec<WrapperBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("fallback_source").forGetter(WrapperBiomeSource::getFallbackSource),
                    Codec.INT.fieldOf("center_x").forGetter(WrapperBiomeSource::getCenterX),
                    Codec.INT.fieldOf("center_z").forGetter(WrapperBiomeSource::getCenterZ),
                    Codec.LONG.fieldOf("radius_sq").forGetter(WrapperBiomeSource::getRadiusSq),
                    Biome.CODEC.fieldOf("biome").forGetter(WrapperBiomeSource::getAppalachianBiome) // La riga della discordia, corretta.
            ).apply(instance, WrapperBiomeSource::new)
    );

    // I CAMPI SONO GLI STESSI DI PRIMA
    private final BiomeSource fallbackSource;
    private final int centerX;
    private final int centerZ;
    private final long radiusSq;
    private final Holder<Biome> appalachianBiome;

    // IL COSTRUTTORE È LO STESSO
    public WrapperBiomeSource(BiomeSource fallbackSource, int centerX, int centerZ, long radiusSq, Holder<Biome> appalachianBiome) {
        super();
        this.fallbackSource = fallbackSource;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radiusSq = radiusSq;
        this.appalachianBiome = appalachianBiome;
    }

    // QUESTO È GIUSTO E RESTA UGUALE
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        long distSq = (long)(x - centerX) * (x - centerX) + (long)(z - centerZ) * (z - centerZ);
        return distSq <= this.radiusSq ? this.appalachianBiome : this.fallbackSource.getNoiseBiome(x, y, z, sampler);
    }

    // QUESTO È GIUSTO E RESTA UGUALE
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    // --- GETTERS PER IL CODEC ---
    // Aggiungiamo dei getter pubblici così il Codec può accedervi in modo pulito.
    // Questo risolve l'errore "non esiste".
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
package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class WrapperBiomeSource extends BiomeSource {

    public static final Codec<WrapperBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("fallback_source").forGetter(WrapperBiomeSource::getFallbackSource),
                    Codec.LONG.fieldOf("radius_sq").forGetter(WrapperBiomeSource::getRadiusSq),
                    Biome.CODEC.fieldOf("biome").forGetter(WrapperBiomeSource::getAppalachianBiome),
                    Biome.CODEC.fieldOf("appalachian_stream_biome").forGetter(WrapperBiomeSource::getAppalachianStreamBiome)
            ).apply(instance, WrapperBiomeSource::new)
    );

    private final BiomeSource fallbackSource;
    private final int centerX;
    private final int centerZ;
    private final long radiusSq;
    private final Holder<Biome> appalachianBiome;
    private final Holder<Biome> appalachianStreamBiome;

    public WrapperBiomeSource(BiomeSource fallbackSource, long radiusSq, Holder<Biome> appalachianBiome, Holder<Biome> appalachianStreamBiome) {
        super();
        float angle = (float) (Math.random() * 2 * Math.PI);
        int distance = (int) (4000 + Math.random() * 2001); // Range 4000-6000
        this.fallbackSource = fallbackSource;
        this.centerX = (int) (Math.cos(angle) * distance);
        this.centerZ = (int) (Math.sin(angle) * distance);
        System.out.println("SYNERGO BIOME SPAWNED AT X: " + this.centerX + ", Z: " + this.centerZ);
        this.radiusSq = radiusSq;
        this.appalachianBiome = appalachianBiome;
        this.appalachianStreamBiome = appalachianStreamBiome;
    }

    // ECCO LA LOGICA CHE FUNZIONA
    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        int blockX = QuartPos.toBlock(pX);
        int blockZ = QuartPos.toBlock(pZ);

        long distSq = (long)(blockX - this.centerX) * (long)(blockX - this.centerX) +
                (long)(blockZ - this.centerZ) * (long)(blockZ - this.centerZ);

        // PRIMO CONTROLLO: siamo fuori dal cerchio? Se sì, fanculo, usa il bioma vanilla e non rompere il cazzo.
        if (distSq > this.radiusSq) {
            return this.fallbackSource.getNoiseBiome(pX, pY, pZ, pSampler);
        }

        // OK, SIAMO DENTRO IL CERCHIO.
        Holder<Biome> originalBiome = this.fallbackSource.getNoiseBiome(pX, pY, pZ, pSampler);

        // SECONDO CONTROLLO: il bioma originale è un fottuto fiume o una spiaggia?
        // Se sì, piazzaci il nostro APPALACHIAN_STREAM.
        if (isRiverOrBeach(originalBiome)) {
            //return this.appalachianStreamBiome;
            return originalBiome;
        }

        // TERZO CONTROLLO: il bioma originale è un oceano?
        // Se sì, lascialo stare. Non vogliamo i nostri alberi in mezzo al Pacifico.
        if (isOcean(originalBiome)) {
            return originalBiome;
        }

        // Se non è un fiume, non è una spiaggia e non è un oceano, ALLORA è il nostro APPALACHIAN_FOREST.
        return this.appalachianBiome;
    }

    private boolean isRiverOrBeach(Holder<Biome> biome) {
        return biome.unwrap().map(key -> {
            String path = key.location().getPath();
            return path.contains("river") || path.contains("beach");
        }, (direct) -> false);
    }

    private boolean isOcean(Holder<Biome> biome) {
        return biome.unwrap().map(key -> key.location().getPath().contains("ocean"), (direct) -> false);
    }

    // Non toccare il resto. Va bene così.
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(this.fallbackSource.possibleBiomes().stream(), Stream.of(this.appalachianBiome, this.appalachianStreamBiome));
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }
    // ... getter ...
    public BiomeSource getFallbackSource() { return fallbackSource; }
    public long getRadiusSq() { return radiusSq; }
    public Holder<Biome> getAppalachianBiome() { return appalachianBiome; }
    public Holder<Biome> getAppalachianStreamBiome() { return appalachianStreamBiome; }
}
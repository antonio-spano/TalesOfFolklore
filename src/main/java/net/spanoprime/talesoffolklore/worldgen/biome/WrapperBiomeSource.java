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
                    Biome.CODEC.fieldOf("biome").forGetter(WrapperBiomeSource::getAppalachianBiome)
            ).apply(instance, WrapperBiomeSource::new)
    );

    private final BiomeSource fallbackSource;
    // Rimuoviamo 'final' perché verranno inizializzate dopo.
    private int centerX;
    private int centerZ;
    private final long radiusSq;
    private final Holder<Biome> appalachianBiome;

    // Aggiungiamo un flag per controllare se abbiamo già calcolato il centro.
    // 'volatile' per sicurezza in ambienti multithread.
    private volatile boolean isCenterInitialized = false;

    public WrapperBiomeSource(BiomeSource fallbackSource, long radiusSq, Holder<Biome> appalachianBiome) {
        super();
        this.fallbackSource = fallbackSource;
        this.radiusSq = radiusSq;
        this.appalachianBiome = appalachianBiome;
        // Il costruttore ora è pulito. Non calcola più nulla.
    }

    // Aggiungiamo un metodo per inizializzare il centro in modo intelligente.
    // Lo rendiamo 'synchronized' per evitare che più thread lo eseguano contemporaneamente.
    private synchronized void initializeCenter(Climate.Sampler pSampler) {
        // Double-checked locking: controlliamo di nuovo dopo essere entrati nel blocco synchronized.
        if (this.isCenterInitialized) {
            return;
        }

        int maxTries = 100; // Tentiamo 100 volte per evitare un loop infinito.
        for (int i = 0; i < maxTries; i++) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            int distance = (int) (4000 + Math.random() * 2001); // Range 4000-6000

            int candidateX = (int) (Math.cos(angle) * distance);
            int candidateZ = (int) (Math.sin(angle) * distance);

            // Convertiamo le coordinate in "quart" per il sampler dei biomi.
            int quartX = QuartPos.fromBlock(candidateX);
            int quartY = QuartPos.fromBlock(64); // Usiamo un'altezza media (livello del mare)
            int quartZ = QuartPos.fromBlock(candidateZ);

            // Controlliamo il bioma nel punto candidato.
            Holder<Biome> candidateBiome = this.fallbackSource.getNoiseBiome(quartX, quartY, quartZ, pSampler);

            // Se il bioma NON è un oceano, abbiamo trovato il nostro punto!
            if (!isOcean(candidateBiome)) {
                this.centerX = candidateX;
                this.centerZ = candidateZ;
                System.out.println("SYNERGO BIOME SPAWNED AT (non-ocean) X: " + this.centerX + ", Z: " + this.centerZ);
                this.isCenterInitialized = true;
                return; // Usciamo dal metodo.
            }
        }

        // Se dopo 100 tentativi siamo ancora in un oceano, ci arrendiamo e spawniamo lì.
        System.out.println("WARNING: Could not find a non-ocean location after " + maxTries + " tries. Spawning at last checked location.");
        this.isCenterInitialized = true;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        // --- LAZY INITIALIZATION ---
        // Se il centro non è ancora stato calcolato, lo facciamo ora.
        if (!this.isCenterInitialized) {
            initializeCenter(pSampler);
        }

        // Da qui in poi, la logica è la stessa di prima.
        int blockX = QuartPos.toBlock(pX);
        int blockZ = QuartPos.toBlock(pZ);

        long distSq = (long)(blockX - this.centerX) * (long)(blockX - this.centerX) +
                (long)(blockZ - this.centerZ) * (long)(blockZ - this.centerZ);

        if (distSq > this.radiusSq) {
            return this.fallbackSource.getNoiseBiome(pX, pY, pZ, pSampler);
        }

        Holder<Biome> originalBiome = this.fallbackSource.getNoiseBiome(pX, pY, pZ, pSampler);

        if (isRiver(originalBiome)) {
            return originalBiome;
        }

        if (isOcean(originalBiome)) {
            return originalBiome;
        }

        return this.appalachianBiome;
    }

    // ... il resto della classe (isRiverOrBeach, isOcean, getters, etc.) rimane identico ...
    // ...
    private boolean isRiver(Holder<Biome> biome) {
        return biome.unwrap().map(key -> {
            String path = key.location().getPath();
            return path.contains("river");
        }, (direct) -> false);
    }

    private boolean isOcean(Holder<Biome> biome) {
        return biome.unwrap().map(key -> key.location().getPath().contains("ocean"), (direct) -> false);
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(this.fallbackSource.possibleBiomes().stream(), Stream.of(this.appalachianBiome));
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    public BiomeSource getFallbackSource() { return fallbackSource; }
    public long getRadiusSq() { return radiusSq; }
    public Holder<Biome> getAppalachianBiome() { return appalachianBiome; }
}
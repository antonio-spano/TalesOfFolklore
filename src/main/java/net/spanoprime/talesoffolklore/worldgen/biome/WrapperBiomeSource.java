package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos; // <-- IMPORT NECESSARIO
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.spanoprime.talesoffolklore.item.custom.AppalachianMapItem;

import java.util.stream.Stream;

public class WrapperBiomeSource extends BiomeSource {

    // Il codec non cambia
    public static final Codec<WrapperBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("fallback_source").forGetter(WrapperBiomeSource::getFallbackSource),
                    //Codec.INT.fieldOf("center_x").forGetter(WrapperBiomeSource::getCenterX),
                    //Codec.INT.fieldOf("center_z").forGetter(WrapperBiomeSource::getCenterZ),
                    Codec.LONG.fieldOf("radius_sq").forGetter(WrapperBiomeSource::getRadiusSq),
                    Biome.CODEC.fieldOf("biome").forGetter(WrapperBiomeSource::getAppalachianBiome)
            ).apply(instance, WrapperBiomeSource::new)
    );

    private final BiomeSource fallbackSource;
    private final int centerX;
    private final int centerZ;
    private final int maxCoord = 4000;
    private final int rangeCoord = 2000;
    private final long radiusSq;
    private final Holder<Biome> appalachianBiome;

    // Il costruttore non cambia
    public WrapperBiomeSource(BiomeSource fallbackSource, long radiusSq, Holder<Biome> appalachianBiome) {
        super();
        float angle = (float) (Math.random() * 2 * Math.PI);
        int distance = (int) (maxCoord + Math.random() * (rangeCoord + 1));
        this.fallbackSource = fallbackSource;
        this.centerX = (int) (Math.cos(angle) * distance);
        this.centerZ = (int) (Math.sin(angle) * distance);
        System.out.println("SYNERGO X: " + this.centerX);
        System.out.println("SYNERGO Z: " + this.centerZ);
        //AppalachianMapItem.coordX = this.centerX;
        //AppalachianMapItem.coordZ = this.centerZ;

        this.radiusSq = radiusSq;
        this.appalachianBiome = appalachianBiome;
    }

    // Il metodo che abbiamo aggiunto prima non cambia
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(this.fallbackSource.possibleBiomes().stream(), Stream.of(this.appalachianBiome));
    }

    // ECCO LA CORREZIONE FINALE. SOLO QUI.
    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        // Convertiamo le quart-coordinate (pX, pZ) in coordinate a blocchi
        int blockX = QuartPos.toBlock(pX);
        int blockZ = QuartPos.toBlock(pZ);

        // Ora il confronto è corretto (blocchi vs blocchi)
        long distSq = (long)(blockX - this.centerX) * (long)(blockX - this.centerX) + (long)(blockZ - this.centerZ) * (long)(blockZ - this.centerZ);

        if (distSq <= this.radiusSq) {
            return this.appalachianBiome;
        } else {
            return this.fallbackSource.getNoiseBiome(pX, pY, pZ, pSampler);
        }
    }

    // Il resto della classe non cambia
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

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
package net.spanoprime.talesoffolklore.worldgen.noise;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import com.google.common.collect.ImmutableList;
import net.spanoprime.talesoffolklore.util.VoronoiGenerator;
/*
* Ly Alex
* */

public class ModBiomeRarity {
    // Puoi parametrizzare questi valori per la tua mod
    private static final List<Integer> BIOME_OCTAVES = ImmutableList.of(0);
    private static final PerlinSimplexNoise NOISE_X = new PerlinSimplexNoise(new XoroshiroRandomSource(1234L), BIOME_OCTAVES);
    private static final PerlinSimplexNoise NOISE_Z = new PerlinSimplexNoise(new XoroshiroRandomSource(4321L), BIOME_OCTAVES);
    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);

    private static double biomeSize = 700.0D; // dimensione regione/quadrante in quart
    private static double separationDistance = 4000.0D; // distanza minima fra centri

    public static void init(double biomeSize_, double separationDistance_) {
        biomeSize = biomeSize_;
        separationDistance = separationDistance_;
    }

    // Dice se quella cella/quart contiene un centro speciale per il seed
    public static VoronoiGenerator.VoronoiInfo getMythicBiomeInfo(long worldSeed, int x, int z) {
        VORONOI_GENERATOR.setSeed(worldSeed);
        double sampleX = x / separationDistance;
        double sampleZ = z / separationDistance;

        double positionOffsetX = 200.0D * NOISE_X.getValue(sampleX, sampleZ, false);
        double positionOffsetZ = 200.0D * NOISE_Z.getValue(sampleX, sampleZ, false);

        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);
        if (info.distance() < (biomeSize / separationDistance)) {
            return info;
        }
        return null;
    }
}

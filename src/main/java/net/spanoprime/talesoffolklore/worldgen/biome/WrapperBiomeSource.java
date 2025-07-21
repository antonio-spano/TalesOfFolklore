package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.biome.Climate.Sampler;

import java.util.stream.Stream;

/**
 * BiomeSource “wrapper” che inserisce l’Appalachian Forest solo nell’Overworld
 * multibioma.  Qualsiasi preset che utilizza un BiomeSource NON MultiNoise
 * (Super-flat, Single-biome, The End, ecc.) viene lasciato intatto.
 */
public class WrapperBiomeSource extends BiomeSource {

    /* ──────────────────── Codec (per JSON world-preset) ──────────────────── */
    public static final Codec<WrapperBiomeSource> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    BiomeSource.CODEC.fieldOf("fallback_source")
                            .forGetter(w -> w.fallback),
                    Codec.LONG.fieldOf("radius_sq")
                            .forGetter(w -> w.radiusSq),
                    Biome.CODEC.fieldOf("biome")
                            .forGetter(w -> w.appalachian)
            ).apply(inst, WrapperBiomeSource::new));

    /* ──────────────────── Campi ──────────────────── */
    private final BiomeSource  fallback;
    private final long         radiusSq;
    private final Holder<Biome> appalachian;

    private int  centerX, centerZ;
    private volatile boolean centerReady;

    /* ──────────────────── Costruttore ──────────────────── */
    public WrapperBiomeSource(BiomeSource fallback,
                              long radiusSq,
                              Holder<Biome> appalachian) {

        this.fallback    = fallback;
        this.radiusSq    = radiusSq;
        this.appalachian = appalachian;
    }

    /* ──────────────────── Inizializza il centro una sola volta (lazy) ──────────────────── */
    private synchronized void initCenter(Sampler sampler) {
        if (centerReady) return;

        final int maxTries = 150;
        for (int i = 0; i < maxTries; i++) {
            double ang = Math.random() * (Math.PI * 2);
            int    d   = 4000 + (int)(Math.random() * 2001);   // 4 000 – 6 000 blocchi

            int candX = (int)(Math.cos(ang) * d);
            int candZ = (int)(Math.sin(ang) * d);

            /* Scarta oceani e isolette (< ~200 blocchi di diametro). */
            if (isOcean(biomeAt(candX, candZ, sampler))) continue;
            if (!hasLandPatch(candX, candZ, 100, sampler)) continue;

            centerX = candX;
            centerZ = candZ;
            centerReady = true;
            System.out.printf("[TOF] Appalachian center @ (%d, %d)%n", centerX, centerZ);
            return;
        }
        /* Fallback (0,0) se non trovato. */
        centerReady = true;
        System.out.println("[TOF] Appalachian center defaulted to (0,0)");
    }

    /* ──────────────────── Query biome ──────────────────── */
    @Override
    public Holder<Biome> getNoiseBiome(int qx, int qy, int qz, Sampler sampler) {

        if (!centerReady) initCenter(sampler);

        int bx = QuartPos.toBlock(qx);
        int bz = QuartPos.toBlock(qz);
        long dsq = (long)(bx - centerX) * (bx - centerX)
                + (long)(bz - centerZ) * (bz - centerZ);

        if (dsq > radiusSq)                           // fuori raggio → vanilla
            return fallback.getNoiseBiome(qx, qy, qz, sampler);

        Holder<Biome> orig = fallback.getNoiseBiome(qx, qy, qz, sampler);
        if (isRiver(orig) || isOcean(orig)) return orig;

        return appalachian;
    }

    /* ──────────────────── Helper ──────────────────── */
    private Holder<Biome> biomeAt(int bx, int bz, Sampler s) {
        return fallback.getNoiseBiome(
                QuartPos.fromBlock(bx),
                QuartPos.fromBlock(64),
                QuartPos.fromBlock(bz), s);
    }

    /** True se nei <radius> blocchi attorno (campionati ogni 32) non compare oceano. */
    private boolean hasLandPatch(int cx, int cz, int radius, Sampler s) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx += 32) {
            for (int dz = -radius; dz <= radius; dz += 32) {
                if (dx * dx + dz * dz > r2) continue;
                if (isOcean(biomeAt(cx + dx, cz + dz, s))) return false;
            }
        }
        return true;
    }

    private boolean isRiver(Holder<Biome> b) { return pathContains(b, "river"); }
    private boolean isOcean(Holder<Biome> b) { return pathContains(b, "ocean"); }

    private static boolean pathContains(Holder<Biome> b, String sub) {
        return b.unwrap().map(k -> k.location().getPath().contains(sub), d -> false);
    }

    /* ──────────────────── Boilerplate ──────────────────── */
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(fallback.possibleBiomes().stream(),
                Stream.of(appalachian));
    }

    @Override
    protected Codec<? extends BiomeSource> codec() { return CODEC; }
}

package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.NoiseRouter;

public class ModLandFinder {

    // Ritorna true se la height vanilla qui è sopra il livello mare
    public static boolean isLand(ServerLevel level, int blockX, int blockZ) {
        // Ottieni stato della worldgen random
        RandomState state = level.getChunkSource().randomState();

        // Ricava il router per il noise (usato per l'altezza)
        NoiseRouter router = state.router();
        DensityFunction initialDensity = router.initialDensityWithoutJaggedness();

        // MinY e MaxY della world dalla dimensione
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        int seaLevel = level.getSeaLevel();

        // Cerchiamo da su verso giù la prima Y "solida"
        for (int y = maxY - 1; y >= minY; --y) {
            double density = initialDensity.compute(new DensityFunction.SinglePointContext(blockX, y, blockZ));
            if (density > 0.390625D) {
                return y > seaLevel; // >62 di solito
            }
        }
        return false; // Ocean/trench
    }
}

package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;

public class ModLandFinder {

    /**
     * Restituisce true se, a partire dalla y massima fino al livello del mare,
     * c’è almeno un punto “solido” secondo il noise router vanilla.
     */
    public static boolean isLandByNoise(ServerLevel level, int blockX, int blockZ) {
        RandomState state = level.getChunkSource().randomState();
        NoiseRouter router = state.router();
        DensityFunction df = router.initialDensityWithoutJaggedness();

        int sea = level.getSeaLevel();
        int maxY = level.getMaxBuildHeight();

        // scendiamo da maxY-1 fino a sea+1
        for (int y = maxY - 1; y > sea; y--) {
            double d = df.compute(new DensityFunction.SinglePointContext(blockX, y, blockZ));
            if (d > 0.0) {
                return true;
            }
        }
        return false;
    }

}

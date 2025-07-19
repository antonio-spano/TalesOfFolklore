package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StreamCarverFeature extends Feature<NoneFeatureConfiguration> {

    // PARAMETRI CHE PUOI MODIFICARE
    private static final int MAX_LENGTH = 128;   // Lunghezza massima in blocchi
    private static final int MAX_WIDTH = 5;      // Larghezza in blocchi. Questo è il tuo 5-6.
    private static final int CARVE_DEPTH = 3;    // Quanto scaviamo verso il basso

    public StreamCarverFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 1. SCEGLIERE UN PUNTO DI PARTENZA
        BlockPos startPos = origin.offset(random.nextInt(16), 0, random.nextInt(16));
        int startY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, startPos.getX(), startPos.getZ());

        // Non partiamo se siamo troppo in alto o troppo in basso
        if (startY < level.getSeaLevel() + 2 || startY > level.getMaxBuildHeight() - 20) {
            return false;
        }

        // 2. DISEGNARE IL PERCORSO
        double currentX = startPos.getX() + 0.5;
        double currentZ = startPos.getZ() + 0.5;
        float angle = random.nextFloat() * Mth.TWO_PI;

        for (int i = 0; i < MAX_LENGTH; i++) {
            // Avanza di un blocco in una direzione
            currentX += Mth.cos(angle);
            currentZ += Mth.sin(angle);
            // Aggiungi una leggera curvatura casuale
            angle += (random.nextFloat() - 0.5F) * 0.7F;

            BlockPos currentBlockPos = new BlockPos((int)currentX, 0, (int)currentZ);
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, currentBlockPos.getX(), currentBlockPos.getZ());

            // 3. SCAVARE CON PRECISIONE
            carveStreamSegment(level, new BlockPos(currentBlockPos.getX(), surfaceY, currentBlockPos.getZ()), MAX_WIDTH);
        }
        return true;
    }

    private void carveStreamSegment(WorldGenLevel level, BlockPos center, int width) {
        int radius = (width - 1) / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Crea una forma circolare per il letto del fiume
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }

                mutable.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                // Scava verso il basso, sostituendo tutto con aria
                for (int dy = 1; dy > -CARVE_DEPTH; dy--) {
                    level.setBlock(mutable.move(0, -1, 0), Blocks.AIR.defaultBlockState(), 2);
                }

                // Ora piazza i blocchi corretti dal basso verso l'alto
                // Livello -3: Ghiaia (il fondo)
                level.setBlock(mutable, Blocks.GRAVEL.defaultBlockState(), 2);
                // Livello -2: Acqua (profondità 1)
                level.setBlock(mutable.move(0, 1, 0), Blocks.WATER.defaultBlockState(), 2);
                // Livello -1 e 0: Aria per assicurare che non ci siano blocchi solidi sopra l'acqua
                level.setBlock(mutable.move(0, 1, 0), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(mutable.move(0, 1, 0), Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }
}
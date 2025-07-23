package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModMossyStreambankRocksBlock;

import java.util.Random;

/**
 * Carves fiumi lunghi e sinuosi basati su “noise” trigonometriche anziché percorsi random‐walk per‐chunk.
 */
public class StreamCarverFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_WIDTH        = 3;    // diametro (dispari)
    private static final int CARVE_DEPTH      = 5;    // profondità incisione
    private static final int BANK_THICKNESS   = 1;    // spessore sponde

    // Parametri “noise”
    private static final double NOISE_SCALE     = 0.1;  // ingrandimento del pattern (più piccolo = fiumi più ampi)
    private static final double NOISE_THRESHOLD = 0.05;  // soglia per definire il canale

    public StreamCarverFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level   = context.level();
        ChunkPos       chunk  = new ChunkPos(context.origin());
        int            minX   = chunk.getMinBlockX();
        int            minZ   = chunk.getMinBlockZ();

        // Per ogni x,z del chunk:
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minX + dx;
                int worldZ = minZ + dz;

                // una “noise” semplice: somma di sin/cos genera curve regolari e continue
                double fx    = worldX * NOISE_SCALE;
                double fz    = worldZ * NOISE_SCALE;
                double noise = (Math.sin(fx + fz) + Math.sin(fx - fz)) * 0.5;

                // se siamo abbastanza “vicini” allo zero del pattern, scaviamo il fiume
                if (Math.abs(noise) > NOISE_THRESHOLD) continue;

                int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldX, worldZ);
                if (surfaceY <= level.getMinBuildHeight() + CARVE_DEPTH) continue;

                carveStreamSegment(level, new BlockPos(worldX, surfaceY, worldZ), MAX_WIDTH);
            }
        }

        return true;
    }

    private void carveStreamSegment(WorldGenLevel level, BlockPos center, int width) {
        int radius = (width - 1) / 2;
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        // 1. Solco + acqua
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx*dx + dz*dz > radius*radius) continue;
                mut.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                // evita cave e fuori altezza
                if (level.isOutsideBuildHeight(mut) || level.getBlockState(mut.below()).isAir()) continue;

                // TREE-SAFE: non passam sotto alberi
                BlockState here  = level.getBlockState(mut);
                BlockState above = level.getBlockState(mut.above());
                if (here.is(BlockTags.LOGS) || here.is(BlockTags.LEAVES)
                        || above.is(BlockTags.LOGS)  || above.is(BlockTags.LEAVES)) {
                    continue;
                }

                // scava
                for (int d = 0; d <= CARVE_DEPTH; d++) {
                    level.setBlock(mut, Blocks.AIR.defaultBlockState(), 2);
                    mut.move(0, -1, 0);
                }

                // letto di ghiaia/roccia
                mut.move(0, 1, 0);
                if (level.getRandom().nextBoolean()) {
                    level.setBlock(mut, ModBlocks.STREAMBED_ROCKS.get().defaultBlockState(), 2);
                } else {
                    level.setBlock(mut, Blocks.GRAVEL.defaultBlockState(), 2);
                }

                // acqua e piante acquatiche
                BlockPos waterPos = mut.move(0, 1, 0).immutable();
                level.setBlock(waterPos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 2);
                if (level.getRandom().nextInt(3) == 0) {
                    level.setBlock(waterPos.above(), Blocks.SEAGRASS.defaultBlockState(), 2);
                }
                level.scheduleTick(waterPos, Fluids.WATER, 0);
                updateNeighbours(level, waterPos);
            }
        }

        // 2. Sponde in pietra/mossy
        int bankR = radius + BANK_THICKNESS;
        RandomSource rnd = RandomSource.create(center.asLong());
        for (int dx = -bankR; dx <= bankR; dx++) {
            for (int dz = -bankR; dz <= bankR; dz++) {
                int dist2 = dx*dx + dz*dz;
                if (dist2 <= radius*radius || dist2 > bankR*bankR) continue;

                mut.set(center.getX() + dx, center.getY() - 1, center.getZ() + dz);
                if (level.isOutsideBuildHeight(mut)) continue;

                BlockState current = level.getBlockState(mut);
                if (!current.isAir() && !current.is(Blocks.GRAVEL) && !current.is(Blocks.WATER)
                        && !current.is(BlockTags.LOGS) && !current.is(BlockTags.LEAVES)) {
                    level.setBlock(mut,
                            ModBlocks.MOSSY_STREAMBANK_ROCKS.get()
                                    .defaultBlockState()
                                    .setValue(ModMossyStreambankRocksBlock.VARIANT, Mth.nextInt(rnd, 0, 1)),
                            2
                    );
                }
            }
        }
    }

    private static void updateNeighbours(WorldGenLevel level, BlockPos pos) {
        // obbliga a ricalcolare flusso e collisioni
        level.neighborShapeChanged(Direction.DOWN,  level.getBlockState(pos.above()), pos, pos.above(), 2, 512);
        level.neighborShapeChanged(Direction.UP,    level.getBlockState(pos.below()), pos, pos.below(), 2, 512);
        level.neighborShapeChanged(Direction.NORTH, level.getBlockState(pos.south()), pos, pos.south(), 2, 512);
        level.neighborShapeChanged(Direction.SOUTH, level.getBlockState(pos.north()), pos, pos.north(), 2, 512);
        level.neighborShapeChanged(Direction.EAST,  level.getBlockState(pos.west()),  pos, pos.west(),  2, 512);
        level.neighborShapeChanged(Direction.WEST,  level.getBlockState(pos.east()),  pos, pos.east(),  2, 512);
    }
}

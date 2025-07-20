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

/**
 * Carves a meandering shallow stream and lines its banks with custom cobblestone, ensuring
 * the water sits slightly lower than the surrounding terrain while keeping a clean edge.
 *
 * **Tree‑safe**: the carver skips any column whose surface or the block above
 * is tagged as LOGS or LEAVES, preventing the creation of floating trees.
 */
public class StreamCarverFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_WIDTH = 5;          // Odd diameter of the circular cross‑section
    private static final int CARVE_DEPTH = 3;        // Depth of the channel (blocks)
    private static final int BANK_THICKNESS = 1;     // Stone rim thickness

    private static final int PATH_LENGTH_PER_CHUNK = 4096; // Random‑walk steps per chunk

    public StreamCarverFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos       chunkPos = new ChunkPos(context.origin());

        RandomSource random = RandomSource.create(
                level.getSeed() + chunkPos.x * 341873128712L + chunkPos.z * 132897987541L + 12345);

        double currentX = chunkPos.getMinBlockX() + random.nextDouble() * 16.0;
        double currentZ = chunkPos.getMinBlockZ() + random.nextDouble() * 16.0;
        float  angle    = random.nextFloat() * Mth.TWO_PI;

        for (int i = 0; i < PATH_LENGTH_PER_CHUNK; i++) {
            currentX += Mth.cos(angle);
            currentZ += Mth.sin(angle);
            angle    += (random.nextFloat() - 0.5F) * 0.4F;

            BlockPos currentPos = new BlockPos((int) currentX, 0, (int) currentZ);

            // keep within 3×3 chunk square around origin
            if (Math.abs(chunkPos.x - (currentPos.getX() >> 4)) > 1 ||
                    Math.abs(chunkPos.z - (currentPos.getZ() >> 4)) > 1) {
                continue;
            }

            int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, currentPos.getX(), currentPos.getZ());
            if (surfaceY <= level.getMinBuildHeight() + CARVE_DEPTH) continue;

            carveStreamSegment(level, new BlockPos(currentPos.getX(), surfaceY, currentPos.getZ()), MAX_WIDTH);
        }
        return true;
    }

    private void carveStreamSegment(WorldGenLevel level, BlockPos center, int width) {
        int radius = (width - 1) / 2;
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        // 1. Channel & water -------------------------------------------------
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;

                mut.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                // Skip if out of build height or below is air (avoid cave roofs)
                if (level.isOutsideBuildHeight(mut) || level.getBlockState(mut.below()).isAir()) continue;

                // *** TREE‑SAFE CHECK ***
                BlockState surface = level.getBlockState(mut);
                BlockState above   = level.getBlockState(mut.above());
                if (surface.is(BlockTags.LOGS) || surface.is(BlockTags.LEAVES) ||
                        above.is(BlockTags.LOGS)   || above.is(BlockTags.LEAVES)) {
                    continue; // do not carve beneath trees → prevents flying logs
                }

                // Dig down
                for (int i = 0; i <= CARVE_DEPTH; i++) {
                    level.setBlock(mut, Blocks.AIR.defaultBlockState(), 2);
                    mut.move(0, -1, 0);
                }

                // Gravel bed
                mut.move(0, 1, 0);
                level.setBlock(mut, Blocks.GRAVEL.defaultBlockState(), 2);

                // Water surface
                BlockPos waterPos = mut.move(0, 1, 0).immutable();
                BlockState water  = Fluids.WATER.defaultFluidState().createLegacyBlock();
                level.setBlock(waterPos, water, 2);
                level.scheduleTick(waterPos, Fluids.WATER, 0);
                updateNeighbours(level, waterPos);
            }
        }

        // 2. Stone banks ----------------------------------------------------
        int bankRadius = radius + BANK_THICKNESS;
        for (int dx = -bankRadius; dx <= bankRadius; dx++) {
            for (int dz = -bankRadius; dz <= bankRadius; dz++) {
                int dist2 = dx * dx + dz * dz;
                if (dist2 <= radius * radius || dist2 > bankRadius * bankRadius) continue;

                mut.set(center.getX() + dx, center.getY() - 1, center.getZ() + dz);
                if (level.isOutsideBuildHeight(mut)) continue;

                BlockState current = level.getBlockState(mut);
                if (!current.isAir() && !current.is(Blocks.GRAVEL) && !current.is(Blocks.WATER) &&
                        !current.is(BlockTags.LOGS) && !current.is(BlockTags.LEAVES)) {
                    level.setBlock(mut, ModBlocks.RIVERBANK_COBBLESTONE.get().defaultBlockState(), 2);
                }
            }
        }
    }

    private static void updateNeighbours(WorldGenLevel level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        level.neighborShapeChanged(Direction.DOWN,  above, pos, pos.above(), 2, 512);
        level.neighborShapeChanged(Direction.UP,    below, pos, pos.below(), 2, 512);
        level.neighborShapeChanged(Direction.NORTH, level.getBlockState(pos.south()), pos, pos.south(), 2, 512);
        level.neighborShapeChanged(Direction.SOUTH, level.getBlockState(pos.north()), pos, pos.north(), 2, 512);
        level.neighborShapeChanged(Direction.EAST,  level.getBlockState(pos.west()),  pos, pos.west(),  2, 512);
        level.neighborShapeChanged(Direction.WEST,  level.getBlockState(pos.east()),  pos, pos.east(),  2, 512);
    }
}
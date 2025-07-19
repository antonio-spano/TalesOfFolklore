package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class StreamCarverFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_WIDTH = 4;
    private static final int CARVE_DEPTH = 5;
    // --- LUNGHEZZA AUMENTATA PER FIUMI PIÙ LUNGHI ---
    private static final int PATH_LENGTH_PER_CHUNK = 512;

    public StreamCarverFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());

        RandomSource random = RandomSource.create(level.getSeed() + chunkPos.x * 341873128712L + chunkPos.z * 132897987541L + 12345);

        double currentX = chunkPos.getMinBlockX() + random.nextDouble() * 16.0;
        double currentZ = chunkPos.getMinBlockZ() + random.nextDouble() * 16.0;
        float angle = random.nextFloat() * Mth.TWO_PI;

        for (int i = 0; i < PATH_LENGTH_PER_CHUNK; i++) {
            currentX += Mth.cos(angle);
            currentZ += Mth.sin(angle);
            angle += (random.nextFloat() - 0.5F) * 0.4F;

            BlockPos currentPos = new BlockPos((int)currentX, 0, (int)currentZ);

            if (Math.abs(chunkPos.x - (currentPos.getX() >> 4)) > 1 || Math.abs(chunkPos.z - (currentPos.getZ() >> 4)) > 1) {
                continue;
            }

            int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, currentPos.getX(), currentPos.getZ());

            if (surfaceY <= level.getMinBuildHeight() + CARVE_DEPTH) {
                continue;
            }

            carveStreamSegment(level, new BlockPos(currentPos.getX(), surfaceY, currentPos.getZ()), MAX_WIDTH);
        }
        return true;
    }

    private void carveStreamSegment(WorldGenLevel level, BlockPos center, int width) {
        int radius = (width - 1) / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }

                mutable.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                if (level.isOutsideBuildHeight(mutable) || level.getBlockState(mutable.below()).isAir()) {
                    continue;
                }

                for (int i = 0; i <= CARVE_DEPTH; i++) {
                    level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
                    mutable.move(0, -1, 0);
                }

                mutable.move(0, 1, 0);
                level.setBlock(mutable, Blocks.GRAVEL.defaultBlockState(), 2);

                BlockPos waterPos = mutable.move(0, 1, 0).immutable();
                BlockState waterState = Fluids.WATER.defaultFluidState().createLegacyBlock();
                level.setBlock(waterPos, waterState, 2);

                // --- FIX DEFINITIVO CON LE API MODERNE ---
                // Programmiamo il tick per sicurezza
                level.scheduleTick(waterPos, Fluids.WATER, 0);

                // Notifichiamo i vicini usando il nuovo metodo neighborShapeChanged
                // Questo forza l'acqua a scorrere e la ghiaia a cadere.
                BlockState changedState = level.getBlockState(waterPos);
                // Il valore 512 è la massima profondità di ricorsione standard, è un valore sicuro.
                level.neighborShapeChanged(Direction.DOWN, level.getBlockState(waterPos.above()), waterPos, waterPos.above(), 2, 512);
                level.neighborShapeChanged(Direction.UP, level.getBlockState(waterPos.below()), waterPos, waterPos.below(), 2, 512);
                level.neighborShapeChanged(Direction.NORTH, level.getBlockState(waterPos.south()), waterPos, waterPos.south(), 2, 512);
                level.neighborShapeChanged(Direction.SOUTH, level.getBlockState(waterPos.north()), waterPos, waterPos.north(), 2, 512);
                level.neighborShapeChanged(Direction.EAST, level.getBlockState(waterPos.west()), waterPos, waterPos.west(), 2, 512);
                level.neighborShapeChanged(Direction.WEST, level.getBlockState(waterPos.east()), waterPos, waterPos.east(), 2, 512);
            }
        }
    }
}
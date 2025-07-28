package net.spanoprime.talesoffolklore.worldgen.feature;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.material.Fluids;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModMossyStreambankRocksBlock;

public class ModStreamCarverFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_WIDTH        = 5;    // diametro (dispari)
    private static final int CARVE_DEPTH      = 3;    // profondità incisione
    private static final int BANK_THICKNESS   = 1;    // spessore sponde

    // Parametri Perlin
    private static final double NOISE_SCALE     = 0.005  ;   // scala: più piccolo → fiumi più larghi
    private static final double NOISE_THRESHOLD = 0.0025;   // soglia "banda" del fiume

    // Generatore di PerlinNoise condiviso per tutto il mondo
    private static PerlinNoise perlin;
    private static long perlinSeed;

    public ModStreamCarverFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        long seed = level.getSeed();

        if (perlin == null || perlinSeed != seed) {
            perlinSeed = seed;
            perlin = PerlinNoise.create(
                    RandomSource.create(seed),
                    ImmutableList.of(0,1)  // 4 ottave
            );
        }

        ChunkPos chunk = new ChunkPos(context.origin());
        int minX = chunk.getMinBlockX();
        int minZ = chunk.getMinBlockZ();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minX + dx;
                int worldZ = minZ + dz;

                // usa tutti e tre gli assi: y=0
                double nx = worldX * NOISE_SCALE;
                double nz = worldZ * NOISE_SCALE;
                double noise = perlin.getValue(nx, 0.0, nz);

                if (Math.abs(noise) > NOISE_THRESHOLD) continue;

                int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldX, worldZ);
                if (surfaceY <= level.getMinBuildHeight() + CARVE_DEPTH) continue;

                carveStreamSegment(level, new BlockPos(worldX, surfaceY, worldZ));
            }
        }

        return true;
    }


    private void carveStreamSegment(WorldGenLevel level, BlockPos center) {
        int radius = (MAX_WIDTH - 1) / 2;
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        // 1. Solco + acqua
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                mut.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                // evita cave e fuori altezza
                if (level.isOutsideBuildHeight(mut) || level.getBlockState(mut.below()).isAir()) continue;

                // TREE-SAFE: non passam sotto alberi
                BlockState here  = level.getBlockState(mut);
                BlockState above = level.getBlockState(mut.above());
                BlockState currentAbove = level.getBlockState(mut.above().above());
                if (here.is(BlockTags.LOGS) || above.is(BlockTags.LOGS)) {
                    continue;
                }

                if (currentAbove.is(ModBlocks.FIREFLIES_BUSH.get())
                        || currentAbove.is(ModBlocks.UNDERGROWTH.get())
                        || currentAbove.is(ModBlocks.FERN.get())
                        || currentAbove.is(ModBlocks.RED_FUNGUS.get())
                        || currentAbove.is(Blocks.GRASS)
                        || currentAbove.is(ModBlocks.PINE_NEEDLES.get()))
                    level.setBlock(mut.above().above(), Blocks.AIR.defaultBlockState(), 2);

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
                int rand = level.getRandom().nextInt(6);

                if (rand == 0) level.setBlock(waterPos, Blocks.SEAGRASS.defaultBlockState(), 2);
                else if (rand == 1) level.setBlock(waterPos, ModBlocks.CATTAIL.get().defaultBlockState(), 2);

                level.scheduleTick(waterPos, Fluids.WATER, 0);
                updateNeighbours(level, waterPos);
            }
        }

        // 2. Sponde in pietra/mossy
        // --- prima: int bankR = radius + BANK_THICKNESS; …
        int bankR = radius + BANK_THICKNESS;
        RandomSource rnd = RandomSource.create(center.asLong());
        for (int dx = -bankR; dx <= bankR; dx++) {
            for (int dz = -bankR; dz <= bankR; dz++) {
                int dist2 = dx*dx + dz*dz;
                if (dist2 <= radius*radius || dist2 > bankR*bankR) continue;

                // invece di usare center.getY()-1, troviamo qui la Y del terreno
                int wx = center.getX() + dx;
                int wz = center.getZ() + dz;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, wx, wz) - 1;
                BlockPos bankPos = new BlockPos(wx, surfaceY, wz);

                if (level.isOutsideBuildHeight(bankPos)) continue;

                BlockState current = level.getBlockState(bankPos);
                BlockState currentAbove = level.getBlockState(bankPos.above());
                if (!current.isAir() && !current.is(Blocks.GRAVEL) && !current.is(Blocks.WATER)
                        && !current.is(BlockTags.LOGS) && !current.is(BlockTags.LEAVES)) {
                    level.setBlock(bankPos,
                            ModBlocks.MOSSY_STREAMBANK_ROCKS.get()
                                    .defaultBlockState()
                                    .setValue(ModMossyStreambankRocksBlock.VARIANT, Mth.nextInt(rnd, 0, 1)),
                            2
                    );
                }

                if (currentAbove.is(ModBlocks.FIREFLIES_BUSH.get())
                || currentAbove.is(ModBlocks.UNDERGROWTH.get())
                || currentAbove.is(ModBlocks.FERN.get())
                || currentAbove.is(ModBlocks.RED_FUNGUS.get())
                || currentAbove.is(Blocks.GRASS))
                    level.setBlock(bankPos.above(), Blocks.AIR.defaultBlockState(), 2);
            }
        }

    }

    private static void updateNeighbours(WorldGenLevel level, BlockPos pos) {
        level.neighborShapeChanged(Direction.DOWN,  level.getBlockState(pos.above()), pos, pos.above(), 2, 512);
        level.neighborShapeChanged(Direction.UP,    level.getBlockState(pos.below()), pos, pos.below(), 2, 512);
        level.neighborShapeChanged(Direction.NORTH, level.getBlockState(pos.south()), pos, pos.south(), 2, 512);
        level.neighborShapeChanged(Direction.SOUTH, level.getBlockState(pos.north()), pos, pos.north(), 2, 512);
        level.neighborShapeChanged(Direction.EAST,  level.getBlockState(pos.west()),  pos, pos.west(),  2, 512);
        level.neighborShapeChanged(Direction.WEST,  level.getBlockState(pos.east()),  pos, pos.east(),  2, 512);
    }
}

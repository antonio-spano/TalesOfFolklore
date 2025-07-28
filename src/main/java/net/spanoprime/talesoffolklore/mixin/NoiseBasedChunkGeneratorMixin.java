package net.spanoprime.talesoffolklore.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModAppalachianGrassBlock;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {
    @Unique private static final double R_BASE    = BiomeInjector.appalachianRadius;
    @Unique private static final double AMPLITUDE = 4.0;
    @Unique private static final double FREQ      = 0.05;
    @Unique private static final PerlinSimplexNoise NOISE = new PerlinSimplexNoise(
            RandomSource.create(1234567L),
            IntList.of(0,1,2,3)
    );

    @Inject(
            method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;"
                    + "Lnet/minecraft/world/level/StructureManager;"
                    + "Lnet/minecraft/world/level/levelgen/RandomState;"
                    + "Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
            at = @At("TAIL")
    )
    private void onBuildSurface(
            WorldGenRegion world,
            StructureManager structureManager,
            RandomState random,
            ChunkAccess chunk,
            CallbackInfo ci
    ) {
        int seaLevel = world.getSeaLevel() - 1;
        int minY     = 40;
        BlockPos center = BiomeInjector.appalachianCenter;
        int cx = center.getX(), cz = center.getZ();

        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        MutableBlockPos pos = new MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int wx = baseX + dx;
            double offX = wx - cx;
            for (int dz = 0; dz < 16; dz++) {
                int wz = baseZ + dz;
                double offZ = wz - cz;

                // calcola il raggio ondulato base
                double perlin = NOISE.getValue(wx * FREQ, wz * FREQ, false);
                double baseRadius = R_BASE + perlin * AMPLITUDE;

                // per ogni y scendiamo e allarghiamo di 1
                for (int y = seaLevel; y >= minY; y--) {
                    double depth = seaLevel - y;
                    double rLoc   = baseRadius + depth;    // allarghiamo di 'depth' blocchi
                    double thresh = rLoc * rLoc;
                    if (offX*offX + offZ*offZ > thresh) continue;

                    pos.set(wx, y, wz);
                    if (chunk.getBlockState(pos).getBlock() != Blocks.WATER) continue;

                    BlockState newState;
                    if (depth == 0) {
                        // 90% variante 0, 10% le altre
                        double r = Math.random();
                        int variant = r < .9 ? 0
                                : r < .9333 ? 1
                                : r < .9666 ? 2
                                : 3;
                        newState = ModBlocks.DAMP_GRASS_BLOCK.get()
                                .defaultBlockState()
                                .setValue(ModAppalachianGrassBlock.VARIANT, variant);
                    } else if (depth <= 4) {
                        newState = ModBlocks.DAMP_DIRT.get().defaultBlockState();
                    } else {
                        newState = ModBlocks.APPALACHIAN_STONE.get().defaultBlockState();
                    }

                    chunk.setBlockState(pos, newState, false);
                }
            }
        }
    }
}

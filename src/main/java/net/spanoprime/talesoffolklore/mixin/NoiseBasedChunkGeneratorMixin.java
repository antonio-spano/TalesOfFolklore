package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
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
    // raggio al quadrato
    @Unique private static final long R2 = (long)BiomeInjector.appalachianRadius * BiomeInjector.appalachianRadius;

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
        int minY     = 40; // come nel tuo esempio
        BlockPos center = BiomeInjector.appalachianCenter;
        int cx = center.getX(), cz = center.getZ();

        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();

        // un solo MutableBlockPos per tutto il chunk
        MutableBlockPos pos = new MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int wx = baseX + dx;
            long dx2 = (long)wx - cx;
            for (int dz = 0; dz < 16; dz++) {
                int wz = baseZ + dz;
                long dz2 = (long)wz - cz;
                if (dx2*dx2 + dz2*dz2 > R2) continue;  // fuori dal cerchio

                // ciclo unico da seaLevel fino a minY
                for (int y = seaLevel; y >= minY; y--) {
                    pos.set(wx, y, wz);
                    if (chunk.getBlockState(pos).getBlock() != Blocks.WATER) continue;

                    // determinazione veloce della profondità
                    int depth = seaLevel - y;
                    BlockState newState;

                    if (depth == 0) {
                        // variante pseudo‐random deterministica basata su coord x/z
                        int variant = (int) (Math.random() * 20);
                        newState = ModBlocks.DAMP_GRASS_BLOCK.get()
                                .defaultBlockState()
                                .setValue(ModAppalachianGrassBlock.VARIANT,
                                        variant == 0 ? 1
                                                : variant == 1 ? 2
                                                : variant == 2 ? 3
                                                : 0);
                    }
                    else if (depth <= 4) {
                        newState = ModBlocks.DAMP_DIRT.get().defaultBlockState();
                    }
                    else {
                        newState = ModBlocks.APPALACHIAN_STONE.get().defaultBlockState();
                    }

                    chunk.setBlockState(pos, newState, false);
                }
            }
        }
    }
}

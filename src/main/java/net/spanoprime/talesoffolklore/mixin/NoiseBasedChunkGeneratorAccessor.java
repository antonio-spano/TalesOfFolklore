package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.StructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

// This accessor makes the private method accessible to our mixin
@Mixin(net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.class)
public interface NoiseBasedChunkGeneratorAccessor {
    @Invoker("createNoiseChunk")
    NoiseChunk talesoffolklore_invokeCreateNoiseChunk(
            ChunkAccess chunk,
            StructureManager structureManager,
            Blender blender,
            RandomState random
    );
}

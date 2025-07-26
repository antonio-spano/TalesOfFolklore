package net.spanoprime.talesoffolklore.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.spanoprime.talesoffolklore.accessor.ModBiomeSourceAccessor;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

    @Inject(
            at = @At("HEAD"),
            method = "generate(" +
                    "Ljava/util/concurrent/Executor;" +
                    "Lnet/minecraft/server/level/ServerLevel;" +
                    "Lnet/minecraft/world/level/chunk/ChunkGenerator;" +
                    "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;" +
                    "Lnet/minecraft/server/level/ThreadedLevelLightEngine;" +
                    "Ljava/util/function/Function;" +
                    "Ljava/util/List;)" +
                    "Ljava/util/concurrent/CompletableFuture;"
    )
    private void onGenerate(
            java.util.concurrent.Executor executor,
            ServerLevel serverLevel,
            ChunkGenerator chunkGenerator,
            StructureTemplateManager templateManager,
            ThreadedLevelLightEngine lightEngine,
            Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> callback,
            List<ChunkAccess> chunks,
            CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir
    ) {
        BiomeSource source = chunkGenerator.getBiomeSource();
        // se il BiomeSource implementa il nostro accessor, espandi con il nostro bioma
        if (source instanceof ModBiomeSourceAccessor accessor) {
            accessor.letsVinoCryptids$modExpandBiome(BiomeInjector.APPALACHIAN_FOREST_HOLDER);
        }
    }
}

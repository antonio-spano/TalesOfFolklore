package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    // Your biome registry key

    // Set your patch here
    @Unique
    private static final int BIOME_CENTER_X = 10000;
    @Unique
    private static final int BIOME_CENTER_Z = -8000;
    @Unique
    private static final int BIOME_RADIUS = 1000;

    // Helper to check if position is within the custom biome patch
    @Unique
    private static boolean letsVinoCryptids$isInPatch(int x, int z) {
        double dx = x - BIOME_CENTER_X;
        double dz = z - BIOME_CENTER_Z;
        return dx * dx + dz * dz <= BIOME_RADIUS * BIOME_RADIUS;
    }

    /**
     * Patch doCreateBiomes to wrap the BiomeResolver used for this chunk
     * and override the biome result within the desired X/Z radius.
     */
    @Inject(method = "doCreateBiomes", at = @At("HEAD"), cancellable = true)
    private void injectCustomBiome(
            Blender blender,
            RandomState random,
            StructureManager structureManager,
            ChunkAccess chunk,
            CallbackInfo ci
    ) {
        final BiomeResolver original = BelowZeroRetrogen.getBiomeResolver(
                blender.getBiomeResolver(((NoiseBasedChunkGenerator) (Object) this).getBiomeSource()), chunk);

        NoiseChunk noiseChunk = chunk.getOrCreateNoiseChunk(
                (c) -> ((NoiseBasedChunkGeneratorAccessor)(Object)this)
                        .talesoffolklore_invokeCreateNoiseChunk(c, structureManager, blender, random)
        );

        Climate.Sampler sampler = ((NoiseChunkAccessor)(Object)noiseChunk)
                .talesoffolklore_invokeCachedClimateSampler(
                        random.router(),
                        ((NoiseBasedChunkGenerator)(Object)this).generatorSettings().value().spawnTarget()
                );

        RegistryAccess registryAccess;
        if (chunk.getWorldForge() != null) {
            registryAccess = chunk.getWorldForge().registryAccess();
        } else {
            registryAccess = null;
        }
        if (registryAccess == null) {
            // Can't inject; no registry, fallback to vanilla
            chunk.fillBiomesFromNoise(original, sampler);
            ci.cancel();
            return;
        }

        BiomeResolver customResolver = (x, y, z, climatePoint) -> {
            int bx = net.minecraft.core.QuartPos.toBlock(x);
            int bz = net.minecraft.core.QuartPos.toBlock(z);
            if (letsVinoCryptids$isInPatch(bx, bz)) {
                Holder<Biome> biome = registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                        .getHolder(ModBiomes.APPALACHIAN_FOREST).orElse(null);
                if (biome != null) {
                    System.out.println("SYNERGO [ToF] Injecting custom biome at: " + bx + " " + bz);
                    return biome;
                }
                else
                {
                    System.out.println("SYNERGO [ToF] Biome HOLDER is null!! Registry has: " + registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.BIOME).keySet());
                }
            }
            return original.getNoiseBiome(x, y, z, climatePoint);
        };

        chunk.fillBiomesFromNoise(customResolver, sampler);
        ci.cancel();
    }

}

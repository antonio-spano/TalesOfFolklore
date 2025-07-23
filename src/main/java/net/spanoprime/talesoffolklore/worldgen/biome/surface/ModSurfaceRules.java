package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModAppalachianGrassBlock;
import net.spanoprime.talesoffolklore.worldgen.ModNoiseParameters;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

public final class ModSurfaceRules {

    private ModSurfaceRules() {} // utility class

    public static SurfaceRules.RuleSource overworldRules() {
        // Blocchi custom
        BlockState GRASS = ModBlocks.DAMP_GRASS_BLOCK.get().defaultBlockState();
        BlockState GRASS0 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 1);
        BlockState GRASS1 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 2);
        BlockState GRASS2 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 3);

        BlockState DIRT  = ModBlocks.DAMP_DIRT.get().defaultBlockState();
        BlockState ROCK  = ModBlocks.APPALACHIAN_STONE.get().defaultBlockState();

        // Regola per scegliere la variante di erba in base al noise (questa non cambia)
        SurfaceRules.RuleSource chooseVariant = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, -1.0D, 0.5D),
                        SurfaceRules.state(GRASS)),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, 0.5D, 0.66D),
                        SurfaceRules.state(GRASS0)),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE,  0.66D, 0.82D),
                        SurfaceRules.state(GRASS1)),
                /* fallback */      SurfaceRules.state(GRASS2)
        );

        // --- MODIFICA CHIAVE QUI ---
        // Surface-rule finale per il tuo bioma
        SurfaceRules.RuleSource appalachianSurface = SurfaceRules.sequence(
                // Regola #1: Gestisce lo strato superficiale (ON_FLOOR)
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                // CASO A: Se siamo sott'acqua, piazza DIRT.
                                // La condizione SurfaceRules.not(SurfaceRules.waterBlockCheck(0, 0))
                                // è vera se il blocco si trova A o SOTTO il livello dell'acqua.
                                SurfaceRules.ifTrue(
                                        SurfaceRules.not(SurfaceRules.waterBlockCheck(0, 0)),
                                        SurfaceRules.state(DIRT)
                                ),
                                // CASO B (fallback): Se non siamo sott'acqua, usa la logica dell'erba.
                                chooseVariant
                        )
                ),
                // Regola #2: Lo strato subito sotto la superficie è sempre DIRT.
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(DIRT)),

                // Regola #3 (fallback): Tutto il resto è ROCK.
                SurfaceRules.state(ROCK)
        );

        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.APPALACHIAN_FOREST), appalachianSurface);
    }
}
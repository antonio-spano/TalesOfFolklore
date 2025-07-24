package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModAppalachianGrassBlock;
import net.spanoprime.talesoffolklore.worldgen.ModNoiseParameters;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

public final class ModSurfaceRules {

    private ModSurfaceRules() {}

    public static SurfaceRules.RuleSource overworldRules() {
        // Stati custom
        BlockState GRASS   = ModBlocks.DAMP_GRASS_BLOCK.get().defaultBlockState();
        BlockState GRASS0  = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 1);
        BlockState GRASS1  = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 2);
        BlockState GRASS2  = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 3);
        BlockState DIRT    = ModBlocks.DAMP_DIRT.get().defaultBlockState();
        BlockState ROCK    = ModBlocks.APPALACHIAN_STONE.get().defaultBlockState();

        // Logica per le varianti di erba
        SurfaceRules.RuleSource chooseVariant = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, -1.0D, 0.5D),
                        SurfaceRules.state(GRASS)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, 0.5D, 0.66D),
                        SurfaceRules.state(GRASS0)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, 0.66D, 0.82D),
                        SurfaceRules.state(GRASS1)
                ),
                // fallback
                SurfaceRules.state(GRASS2)
        );

        // Regole per il bioma Appalachian Forest
        SurfaceRules.RuleSource appalachianSurface = SurfaceRules.sequence(
                // 1) Sostituisci solo sopra la VERA superficie (no grotte!)
                SurfaceRules.ifTrue(
                        SurfaceRules.abovePreliminarySurface(),
                        // 2) poi solo dove ON_FLOOR
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.sequence(
                                        // se non sommerso, metti DIRT
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.not(SurfaceRules.waterBlockCheck(0, 0)),
                                                SurfaceRules.state(DIRT)
                                        ),
                                        // altrimenti usa l'erba variantata
                                        chooseVariant
                                )
                        )
                ),
                // subito sotto la superficie metto sempre DIRT
                SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.state(DIRT)
                ),
                // tutto il resto è roccia
                SurfaceRules.state(ROCK)
        );

        // Applica solo nel tuo bioma
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_FOREST),
                appalachianSurface
        );
    }
}

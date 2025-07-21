package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import com.sun.jna.platform.win32.Variant;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModAppalachianGrassBlock;
import net.spanoprime.talesoffolklore.worldgen.ModNoiseParameters;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

/**
 * Surface-rules per il bioma Appalachian Forest.
 *
 *  ┌────────────┐  ON_FLOOR        →  APPALACHIAN_GRASS_BLOCK
 *  │   Suolo    │  UNDER_FLOOR     →  APPALACHIAN_DIRT (profondità 2)
 *  └────────────┘  tutto il resto  →  RIVERBANK_STONE
 */
public final class ModSurfaceRules {

    private ModSurfaceRules() {} // utility class

    /** Restituisce la RuleSource da inserire nel SurfaceRuleData dello Overworld. */
    public static SurfaceRules.RuleSource overworldRules() {
        //int rand = (int)(Math.random() * 4);
        // Blocchi custom
        BlockState GRASS = ModBlocks.APPALACHIAN_GRASS_BLOCK.get().defaultBlockState();
        BlockState GRASS0 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 1);
        BlockState GRASS1 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 2);
        BlockState GRASS2 = GRASS.setValue(ModAppalachianGrassBlock.VARIANT, 3);

        BlockState DIRT  = ModBlocks.APPALACHIAN_DIRT.get().defaultBlockState();
        BlockState ROCK  = ModBlocks.APPALACHIAN_STONE.get().defaultBlockState();

// Scegli variante in base al rumore “surface”
        SurfaceRules.RuleSource chooseVariant = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, -1.0D, 0.66D),
                        SurfaceRules.state(GRASS)),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE, 0.66D, 0.77D),
                        SurfaceRules.state(GRASS0)),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.VARIANT_NOISE,  0.77D, 0.88D),
                        SurfaceRules.state(GRASS1)),
                /* fallback */      SurfaceRules.state(GRASS2)
        );

// Surface-rule finale per il tuo bioma
        SurfaceRules.RuleSource appalachianSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, chooseVariant),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(DIRT)),
                SurfaceRules.state(ROCK)
        );

        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.APPALACHIAN_FOREST), appalachianSurface);
    }
}

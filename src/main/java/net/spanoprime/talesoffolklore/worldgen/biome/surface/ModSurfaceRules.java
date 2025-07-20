package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.spanoprime.talesoffolklore.block.ModBlocks;
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

        // Blocchi custom
        BlockState GRASS = ModBlocks.APPALACHIAN_GRASS_BLOCK.get().defaultBlockState();
        BlockState DIRT  = ModBlocks.APPALACHIAN_DIRT.get().defaultBlockState();
        BlockState ROCK  = ModBlocks.RIVERBANK_COBBLESTONE.get().defaultBlockState();

        // Condizione: siamo nel bioma Appalachian Forest?
        SurfaceRules.ConditionSource isAppalachian =
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_FOREST);

        // Sequenza di sostituzioni
        SurfaceRules.RuleSource appalachianSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,  SurfaceRules.state(GRASS)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(DIRT)),
                /* fallback dentro il bioma → pietra fluviale */
                SurfaceRules.state(ROCK)
        );

        // Applichiamo la sequenza solo al nostro bioma
        return SurfaceRules.ifTrue(isAppalachian, appalachianSurface);
    }
}

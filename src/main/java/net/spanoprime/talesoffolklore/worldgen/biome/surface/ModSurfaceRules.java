package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource AIR = SurfaceRules.state(Blocks.AIR.defaultBlockState());
    private static final SurfaceRules.RuleSource WATER = SurfaceRules.state(Blocks.WATER.defaultBlockState());
    private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
    private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());

    public static SurfaceRules.RuleSource makeRiverOnlyRule() {
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_STREAM),

                SurfaceRules.sequence(
                        // REGOLA 1: Se Y è 63 o più in alto, PIAZZA ARIA. Cancella le montagne.
                        //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0), AIR),

                        // REGOLA 2: Se la regola 1 non ha funzionato (quindi Y=62), PIAZZA ACQUA. Questo è il pelo dell'acqua.
                        //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0), WATER),

                        // REGOLA 3: Se Y=61, PIAZZA GHIAIA. Questo è il letto del fiume.
                        //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(61), 0), GRAVEL),

                        // REGOLA 4 (FALLBACK): Se nessuna delle regole di sopra ha funzionato (quindi siamo a Y=60 o PIU' IN GIU'),
                        // RIEMPI TUTTO IL FOTTUTO MONDO SOTTO DI TE DI ROCCIA.
                        //STONE
                )
        );
    }
}
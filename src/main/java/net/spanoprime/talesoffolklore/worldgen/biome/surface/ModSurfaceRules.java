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
        // La regola si attiva SOLO se siamo nel nostro bioma del fiume.
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_STREAM),

                // Esegue questa sequenza di comandi ASSOLUTI, che sovrascrivono qualsiasi cosa.
                SurfaceRules.sequence(

                        // 1. CANCELLA IL CIELO: Se sei SOPRA Y=63, metti ARIA.
                        // Questo distrugge colline e terreno in eccesso che potrebbero trovarsi dentro l'area del fiume.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(63), 0), AIR),

                        // 2. IMPOSTA IL LIVELLO DEL MARE: A Y=63 ESATTO, metti ACQUA.
                        // Questa è la superficie piatta del nostro ruscello.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0), WATER),

                        // 3. IMPOSTA IL LETTO DEL FIUME (Profondità 1): A Y=62 ESATTO, metti GHIAIA.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0), GRAVEL),

                        // 4. RIEMPI TUTTO IL RESTO SOTTO CON PIETRA (Profondità 2 e oltre).
                        // Questa è la regola "World Edit". Qualsiasi blocco la cui Y è 61 o meno
                        // viene FORZATAMENTE trasformato in PIETRA.
                        // Tappa le voragini, crea un fondo solido e piatto.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(62), 0), STONE)
                )
        );
    }
}
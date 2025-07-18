package net.spanoprime.talesoffolklore.worldgen.biome.surface;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
    private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());

    /**
     * Crea e restituisce SOLO la regola per il fiume degli Appalachi.
     * Questa regola verrà poi messa in sequenza prima di quelle vanilla.
     */
    public static SurfaceRules.RuleSource makeRiverOnlyRule() {
        return SurfaceRules.ifTrue(
                // La regola si attiva SOLO se il bioma è il nostro fiume.
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_STREAM),
                // Se la condizione è vera, esegue questa sequenza di costruzione:
                SurfaceRules.sequence(
                        // A Y=62, metti la ghiaia. L'acqua (livello mare Y=63) sarà profonda 1 blocco.
                        // Per un ruscello profondo 2 blocchi, cambia in VerticalAnchor.absolute(61).
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0), GRAVEL),

                        // Subito sotto la ghiaia, a Y=61, mettiamo la terra.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(61), 0), DIRT),

                        // Tutto il resto sotto, di default, è pietra.
                        STONE
                )
        );
    }
}
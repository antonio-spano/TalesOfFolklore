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
        // Questa regola si attiva solo nel bioma appalachian_stream.
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.APPALACHIAN_STREAM),

                // Esegue questa sequenza di regole dall'alto verso il basso.
                // La prima regola che corrisponde vince.
                SurfaceRules.sequence(

                        // 1. Sopra Y=63 (quindi da Y=64 in su), metti ARIA.
                        //    Questo appiattisce il terreno e rimuove le colline.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0), AIR),

                        // 2. A Y=63, metti ACQUA. Questa è la superficie del fiume.
                        //SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0), WATER),

                        // 3. A Y=62, metti ACQUA. Questo crea il secondo blocco di profondità.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0), WATER),

                        // 4. A Y=61, metti GHIAIA. Questo è il letto del fiume.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(61), 0), GRAVEL),

                        // 5. Sotto Y=61 (quindi da Y=60 in giù), metti PIETRA.
                        //    Questo crea un fondo solido e tappa le caverne sottostanti.
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0), STONE)
                )
        );
    }
}
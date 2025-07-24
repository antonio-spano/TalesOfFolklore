package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ModFallenLogFeature extends Feature<ModFallenLogConfiguration> {
    public ModFallenLogFeature(Codec<ModFallenLogConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<ModFallenLogConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource rand = ctx.random();
        ModFallenLogConfiguration cfg = ctx.config();
        BlockPos pos = ctx.origin();
        // … qui copia/incolla la logica vanilla di come posizionano il tronco caduto …
        return true;
    }
}

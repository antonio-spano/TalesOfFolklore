package net.spanoprime.talesoffolklore.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ShallowRiverbedFeature extends Feature<NoneFeatureConfiguration> {

    public ShallowRiverbedFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Livello del mare. L'acqua arriva a Y=62.
        // Vogliamo il letto a Y=61 per avere 1 blocco d'acqua.
        final int targetRiverbedY = 61;
        final BlockState gravel = Blocks.GRAVEL.defaultBlockState();
        final BlockState water = Blocks.WATER.defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placed = false;

        // Itera su ogni colonna del chunk in cui questa feature viene eseguita.
        for (int x = origin.getX(); x < origin.getX() + 16; x++) {
            for (int z = origin.getZ(); z < origin.getZ() + 16; z++) {

                // Prendiamo l'altezza del fondale (ignora l'acqua).
                int groundY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                mutablePos.set(x, groundY, z);

                // Se il fondale è sotto il nostro target Y=61 E c'è acqua sopra...
                if (groundY < targetRiverbedY && level.isWaterAt(mutablePos.above())) {

                    // ...allora riempiamo con la ghiaia dal fondale attuale fino al nostro target.
                    for (int y = groundY; y < targetRiverbedY; y++) {
                        level.setBlock(mutablePos.setY(y), gravel, 2);
                    }

                    // Piazziamo il letto di ghiaia finale a Y=61.
                    level.setBlock(mutablePos.setY(targetRiverbedY), gravel, 2);

                    // Piazziamo 1 blocco d'acqua a Y=62.
                    level.setBlock(mutablePos.setY(targetRiverbedY + 1), water, 2);

                    // Ci assicuriamo che sopra l'acqua ci sia aria per evitare problemi.
                    level.setBlock(mutablePos.setY(targetRiverbedY + 2), air, 2);

                    placed = true;
                }
            }
        }
        return placed;
    }
}
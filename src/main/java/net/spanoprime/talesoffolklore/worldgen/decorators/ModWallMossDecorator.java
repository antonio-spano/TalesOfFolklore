package net.spanoprime.talesoffolklore.worldgen.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModWallMossBlock;
import net.spanoprime.talesoffolklore.worldgen.*;

public class ModWallMossDecorator extends TreeDecorator {

    public static final Codec<ModWallMossDecorator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("probability").forGetter(d -> d.probability)
            ).apply(instance, ModWallMossDecorator::new)
    );

    private final float probability;
    private int threshold = 5;

    public ModWallMossDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.WALL_MOSS.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        BlockState moss = ModBlocks.WALL_MOSS.get().defaultBlockState();

        int minY = context.logs().stream().mapToInt(BlockPos::getY).min().orElse(Integer.MAX_VALUE);

        for (BlockPos logPos : context.logs()) {
            if (logPos.getY() >= minY + threshold) {
                // Questo ciclo usa Direction.Plane.HORIZONTAL, quindi "direction" sarà sempre N, S, W, o E. Perfetto.
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < this.probability) {
                        BlockPos targetPos = logPos.relative(direction);

                        if (context.isAir(targetPos)
                                && !context.logs().contains(targetPos)
                                && !context.leaves().contains(targetPos)) {

                            // --- LA CORREZIONE È QUI ---
                            // VECCHIA RIGA (SBAGLIATA):
                            // context.setBlock(targetPos, moss.setValue(BlockStateProperties.FACING, direction));

                            // NUOVA RIGA (CORRETTA):
                            // Usiamo la proprietà FACING definita DENTRO la nostra classe del blocco.
                            context.setBlock(targetPos, moss.setValue(ModWallMossBlock.FACING, direction));
                        }
                    }
                }
            }
        }
    }

}
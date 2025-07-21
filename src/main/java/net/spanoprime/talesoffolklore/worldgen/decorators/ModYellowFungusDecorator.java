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
import net.spanoprime.talesoffolklore.block.custom.ModYellowFungusBlock;
import net.spanoprime.talesoffolklore.worldgen.*;

public class ModYellowFungusDecorator extends TreeDecorator {

    public static final Codec<ModYellowFungusDecorator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("probability").forGetter(d -> d.probability)
            ).apply(instance, ModYellowFungusDecorator::new)
    );

    private final float probability;
    private int threshold = 3;

    public ModYellowFungusDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.YELLOW_FUNGUS.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        BlockState fungus = ModBlocks.YELLOW_FUNGUS.get().defaultBlockState();

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
                            context.setBlock(targetPos, fungus.setValue(ModYellowFungusBlock.FACING, direction));
                        }
                    }
                }
            }
        }
    }

}
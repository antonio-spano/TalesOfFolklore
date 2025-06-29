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

public class ModPineFungusDecorator extends TreeDecorator {

    public static final Codec<ModPineFungusDecorator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("probability").forGetter(d -> d.probability)
            ).apply(instance, ModPineFungusDecorator::new)
    );

    private final float probability;

    public ModPineFungusDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.WALL_MOSS.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        BlockState moss = ModBlocks.WALL_MOSS.get().defaultBlockState();

        // Trova la Y minima dei log per sapere dove inizia il tronco
        int minY = context.logs().stream().mapToInt(BlockPos::getY).min().orElse(Integer.MAX_VALUE);

        for (BlockPos logPos : context.logs()) {
            // Applica solo se è dal 4° blocco in su (minY + 3)
            if (logPos.getY() >= minY + 5) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < this.probability) {
                        BlockPos targetPos = logPos.relative(direction);

                        if (context.isAir(targetPos)
                                && !context.logs().contains(targetPos)
                                && !context.leaves().contains(targetPos)) {

                            context.setBlock(targetPos, moss.setValue(BlockStateProperties.FACING, direction));
                        }
                    }
                }
            }
        }
    }

}
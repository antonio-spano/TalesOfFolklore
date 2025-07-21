package net.spanoprime.talesoffolklore.worldgen.decorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModUndergrowthBlock;

public class ModUndergrowthDecorator extends TreeDecorator {
    public static final Codec<ModUndergrowthDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
            .fieldOf("probability").xmap(ModUndergrowthDecorator::new, (decorator) -> decorator.probability).codec();

    private final float probability;

    public ModUndergrowthDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.UNDERGROWTH.get();
    }

    @Override
    public void place(Context pContext) {
        if (pContext.logs().isEmpty()) {
            return;
        }
        BlockPos rootPos = pContext.logs().get(0);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                if (pContext.random().nextFloat() > this.probability) {
                    continue;
                }

                pos.set(rootPos.getX() + x, rootPos.getY(), rootPos.getZ() + z);

                for (int i = 0; i < 10 && pContext.isAir(pos); i++) {
                    pos.move(Direction.DOWN);
                }

                BlockPos groundPos = pos.immutable();
                BlockPos placePos = groundPos.above();

                // --- CORREZIONE API DEFINITIVA ---
                // Non usiamo più getBlockState(). Usiamo isStateAtPosition() con un predicato (una lambda)
                // che controlla se lo stato del blocco è uno dei due che ci interessano.
                boolean isTargetGround = pContext.level().isStateAtPosition(groundPos, (state) ->
                        state.is(Blocks.PODZOL) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.GRASS_BLOCK));

                // La condizione ora usa la nuova variabile booleana.
                if (isTargetGround && pContext.isAir(placePos)) {
                    BlockState needleState = ModBlocks.UNDERGROWTH.get().defaultBlockState();
                    pContext.setBlock(placePos, needleState);
                }
            }
        }
    }
}
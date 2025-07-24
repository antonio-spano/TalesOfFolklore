package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluids;
import net.spanoprime.talesoffolklore.block.ModBlocks;

public class ModAppalachianGrassBlock extends GrassBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 4);

    public ModAppalachianGrassBlock(Properties p_53685_) {
        super(p_53685_);
        this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, 0).setValue(SNOWY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // Non serve chiamare super, la classe GrassBlock lo fa già.
        pBuilder.add(VARIANT, SNOWY);
    }

    /**
     * Metodo corretto per impostare uno stato iniziale quando il blocco viene piazzato.
     * È più pulito e sicuro di onPlace per questo scopo.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        int randomVariant = pContext.getLevel().getRandom().nextInt(5); // 0-4
        return this.defaultBlockState().setValue(VARIANT, randomVariant);
    }

    /**
     * Questo metodo viene chiamato a intervalli casuali dal gioco.
     * Qui si trova la logica che fa morire e diffondere l'erba.
     */
    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        // Controlla se il blocco può ancora essere erba (luce sufficiente, niente blocco solido sopra)
        if (!canBeGrass(pState, pLevel, pPos)) {
            // --- ECCO LA MODIFICA CHIAVE ---
            // Se non può più essere erba, trasformalo in DAMP_DIRT invece che DIRT.
            pLevel.setBlockAndUpdate(pPos, ModBlocks.DAMP_DIRT.get().defaultBlockState());
        } else {
            // Se può essere erba e non è innevato, prova a diffondersi.
            if (pLevel.getMaxLocalRawBrightness(pPos.above()) >= 9) {
                BlockState blockstate = this.defaultBlockState();

                for(int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pPos.offset(pRandom.nextInt(3) - 1, pRandom.nextInt(5) - 3, pRandom.nextInt(3) - 1);
                    // Assicurati che il blocco target sia DAMP_DIRT e che l'erba possa crescere lì.
                    if (pLevel.getBlockState(blockpos).is(ModBlocks.DAMP_DIRT.get()) && canPropagate(blockstate, pLevel, blockpos)) {
                        // Quando si diffonde, mantiene la sua variante per un look più omogeneo.
                        pLevel.setBlockAndUpdate(blockpos, blockstate.setValue(VARIANT, pState.getValue(VARIANT)));
                    }
                }
            }
        }
    }

    // --- METODI HELPER COPIATI DA VANILLA ---
    // Questi metodi sono necessari per la logica di randomTick. Li copiamo qui perché sono privati nella classe originale.

    private static boolean canBeGrass(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(pLevel, pState, pPos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(pLevel, blockpos));
            return i < pLevel.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        return canBeGrass(pState, pLevel, pPos) && !pLevel.getFluidState(blockpos).is(Fluids.WATER);
    }
}
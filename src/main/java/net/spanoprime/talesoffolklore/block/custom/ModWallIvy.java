package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes; // Import mancante

// Blocco edera normale. Non si espande da solo.
// Viene piazzato solo da ModWallIvySeed.
// Deve avere un muro valido adiacente per sopravvivere.
public class ModWallIvy extends Block {

    // Forma sottile, come le viti, per non avere collisione
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D); // Leggermente dentro il blocco

    public ModWallIvy(Properties pProperties) {
        // Proprietà tipiche per edera/viti: nessun'ombra, nessuna collisione, si rompe subito, suono di pianta
        super(pProperties.noOcclusion().noCollission().instabreak().sound(SoundType.VINE));
    }

    // Forma del blocco (senza collisione di default, ma definiamo la forma visiva)
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE; // Usa la forma definita sopra
    }

    // Opzionale: Rende il blocco trasparente alla luce
    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true; // Lascia passare la luce del cielo
    }

    // Opzionale: Fa sì che non scurisca troppo l'area
    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F; // Massima luminosità, come l'aria (o 0.8F se preferisci un po' d'ombra)
    }

    // Necessario: Controlla se l'edera può rimanere in questa posizione
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        // Deve esserci almeno un muro solido valido accanto (orizzontalmente)
        return hasAdjacentWallSupport(pLevel, pPos);
    }

    // Controlla se c'è un muro valido e solido accanto (orizzontalmente)
    // Questo metodo può essere statico o meno, qui lo mettiamo non statico
    // ma potremmo renderlo statico e passargli pState se servisse.
    private boolean hasAdjacentWallSupport(LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockState neighborState = pLevel.getBlockState(neighborPos);
            // Il blocco vicino deve essere un muro valido E avere una faccia solida verso l'edera
            if (ModWallIvySeed.isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, direction.getOpposite())) {
                return true; // Trovato supporto, può sopravvivere
            }
        }
        return false; // Nessun supporto trovato
    }

    // Necessario: Aggiorna lo stato del blocco quando un vicino cambia
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        // Se il blocco non può più sopravvivere (es. il muro di supporto viene rotto),
        // ritorna aria per distruggere questo blocco di edera.
        if (!this.canSurvive(pState, pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        // Altrimenti, ritorna lo stato corrente (non cambia nulla)
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    // NON ha isRandomlyTicking, quindi non cresce da solo.
}
package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
// Non serve più VineBlock se gestiamo noi la logica base
// import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
// Rimuovi import logger se non lo usi più
// import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.Set;

// Seme dell'edera. Può essere piazzato su terreno/mattoni vicino a un muro.
// Cresce *solo* verticalmente (max 2 blocchi sopra) piazzando ModWallIvy.
public class ModWallIvySeed extends VineBlock { // Cambiato da VineBlock a Block

    // Muri validi su cui l'edera (e il seme) possono attaccarsi
    private static final Set<Block> VALID_WALL_BLOCKS = Set.of(
            Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
            Blocks.BRICKS, // Mattoni sono sia terreno valido che muro valido
            Blocks.STONE,          // Pietra normale
            Blocks.COBBLESTONE,    // Pietrisco
            Blocks.DEEPSLATE,       // Ardesia profonda
            Blocks.DEEPSLATE_BRICKS // Mattoni di ardesia profonda (e varianti)
            // Aggiungi qui altri blocchi se necessario
    );

    // Terreno valido O mattoni su cui il *seme* può essere piazzato
    private static final Set<Block> VALID_GROUND_OR_BRICK_BLOCKS = Set.of(
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.PODZOL,
            Blocks.COARSE_DIRT,
            Blocks.ROOTED_DIRT,
            Blocks.MOSS_BLOCK,
            Blocks.BRICKS // Aggiunto mattoni come base valida
            // Aggiungi qui altri blocchi se necessario
    );

    public ModWallIvySeed(Properties pProperties) {
        super(pProperties.instabreak().sound(SoundType.GRASS).randomTicks()); // Ha bisogno di randomTicks per crescere
        // Nota: Non impostare noOcclusion o noCollision qui, il seme è un piccolo blocco solido.
    }

    // Helper (statico) per controllare se un blocco è un muro valido
    public static boolean isWall(BlockState state) {
        // Controlla prima i tag (più efficiente per gruppi grandi come i log)
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS)) { // Aggiunto Planks come esempio
            return true;
        }
        // Poi controlla la lista specifica
        return VALID_WALL_BLOCKS.contains(state.getBlock());
    }

    // Helper (statico) per controllare se il blocco sotto è valido per il seme
    private static boolean isValidGroundOrBrick(BlockState groundState) {
        return VALID_GROUND_OR_BRICK_BLOCKS.contains(groundState.getBlock());
    }

    // Helper (statico) per controllare se una posizione ha supporto da un muro adiacente
    // Usato sia da canSurvive del seme che dalla logica di crescita per l'edera normale
    private static boolean hasAdjacentWallSupport(LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockState neighborState = pLevel.getBlockState(neighborPos);
            if (isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    // Controlla se il *seme* può essere piazzato o rimanere qui
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        BlockState belowState = pLevel.getBlockState(belowPos);

        // Condizione 1: Deve esserci terreno valido o mattoni sotto
        if (!isValidGroundOrBrick(belowState)) {
            return false;
        }

        // Condizione 2: Deve esserci un muro valido e solido accanto (orizzontalmente)
        return hasAdjacentWallSupport(pLevel, pPos);
    }

    // Aggiorna quando i vicini cambiano, distruggendosi se canSurvive fallisce
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!this.canSurvive(pState, pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    // Il seme ha bisogno di tick casuali per tentare la crescita
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    // Logica di crescita del seme
    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide()) return;

        // Controlla di nuovo la sopravvivenza prima di crescere (non si sa mai)
        if (!this.canSurvive(pState, pLevel, pPos)) {
            // Non dovrebbe succedere se updateShape funziona, ma per sicurezza
            pLevel.destroyBlock(pPos, false);
            return;
        }

        // Probabilità di crescita (es. 1 su 4 per tick) per non essere troppo veloce
        if (pRandom.nextInt(4) != 0) {
            return;
        }

        // --- Logica di crescita verticale ---

        BlockPos posAbove1 = pPos.above();
        BlockPos posAbove2 = pPos.above(2);
        BlockState ivyState = ModBlocks.WALL_IVY.get().defaultBlockState();

        // Tenta di piazzare il primo blocco di edera sopra il seme
        if (pLevel.isEmptyBlock(posAbove1) && hasAdjacentWallSupport(pLevel, posAbove1)) {
            pLevel.setBlock(posAbove1, ivyState, 3); // Piazza il primo blocco

            // Se il primo è stato piazzato (o c'era già?), tenta di piazzare il secondo
            // Controlliamo di nuovo hasAdjacentWallSupport anche per il secondo livello
            if (pLevel.isEmptyBlock(posAbove2) && hasAdjacentWallSupport(pLevel, posAbove2)) {
                // Aggiungi un'altra piccola chance per il secondo blocco? (Opzionale)
                if (pRandom.nextInt(2) == 0) { // Es. 50% di chance di crescere al secondo livello SE il primo è cresciuto
                    pLevel.setBlock(posAbove2, ivyState, 3); // Piazza il secondo blocco
                }
            }
        }
        // Se il primo blocco sopra non è vuoto, ma è GIA' la nostra edera,
        // proviamo comunque a far crescere il secondo livello.
        else if (pLevel.getBlockState(posAbove1).is(ModBlocks.WALL_IVY.get())) {
            if (pLevel.isEmptyBlock(posAbove2) && hasAdjacentWallSupport(pLevel, posAbove2)) {
                if (pRandom.nextInt(2) == 0) { // Chance
                    pLevel.setBlock(posAbove2, ivyState, 3); // Piazza il secondo blocco
                }
            }
        }
    }
}
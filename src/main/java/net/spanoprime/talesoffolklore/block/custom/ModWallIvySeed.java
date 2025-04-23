package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.spanoprime.talesoffolklore.TalesOfFolklore; // Importa la classe principale per il LOGGER
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.Set;

public class ModWallIvySeed extends VineBlock {

    private static final Set<Block> VALID_EXTRA_WALL_BLOCKS = Set.of(
            Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
            Blocks.BRICKS,
            Blocks.STONE,          // Pietra normale
            Blocks.COBBLESTONE// Pietrisco
    );

    public ModWallIvySeed(Properties pProperties) {
        super(pProperties);
    }

    // Helper per controllare se un blocco è un muro valido (statico)
    public static boolean isWall(BlockState state) {
        Block block = state.getBlock();
        // TalesOfFolklore.LOGGER.debug("Checking if {} is Wall...", block.getDescriptionId());
        if (state.is(BlockTags.LOGS)) {
            //TalesOfFolklore.LOGGER.debug(" -> YES (is Log)");
            return true;
        }
        if (VALID_EXTRA_WALL_BLOCKS.contains(block)) {
            //TalesOfFolklore.LOGGER.debug(" -> YES (in extra list)");
            return true;
        }
        // TalesOfFolklore.LOGGER.debug(" -> NO");
        return false;
    }

    // Helper per terreno valido
    private boolean isValidGround(BlockState groundState) {
        return groundState.is(Blocks.DIRT) || groundState.is(Blocks.GRASS_BLOCK);
    }


    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        //TalesOfFolklore.LOGGER.debug("Checking Seed canSurvive at {}", pPos); // Debug inizio
        BlockPos belowPos = pPos.below();
        BlockState belowState = pLevel.getBlockState(belowPos);

        if (!isValidGround(belowState)) {
            return false;
        }
        //TalesOfFolklore.LOGGER.debug(" -> Ground OK");

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pPos.relative(direction);
            BlockState neighborState = pLevel.getBlockState(neighborPos);
            // Il muro deve essere valido E avere una faccia solida verso il seme
            if (isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!this.canSurvive(pState, pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true; // Deve tickare per crescere
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide()) return;

        // Rimuovi/commenta questi log una volta che funziona, per non floodare la console

        if (!this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, false); // Rimuovi se non più valido
            return;
        }

        // Prova a crescere in tutte le 6 direzioni
        for (Direction growDirection : Direction.values()) {
            // TalesOfFolklore.LOGGER.debug(" -> Checking growth direction: {}", growDirection);
            BlockPos targetPos = pPos.relative(growDirection);

            // Condizione 1: Il posto dove mettere l'edera deve essere ARIA
            if (pLevel.isEmptyBlock(targetPos)) {
                //TalesOfFolklore.LOGGER.debug("   -> Target {} is Air. Checking for wall support...", targetPos);

                // Condizione 2: L'edera normale (WallIvy) per essere piazzata in targetPos,
                // deve avere un muro valido e solido adiacente a targetPos (a cui "aggrapparsi").
                // Il seme (pPos) funge da "origine" ma non necessariamente da supporto diretto per la nuova edera.
                boolean foundWallSupport = false;
                BlockPos wallPos = null; // Memorizza la pos del muro
                BlockState wallState = null; // Memorizza lo stato del muro

                for(Direction supportDirection : Direction.values()) {
                    // Non considerare il blocco da cui stiamo crescendo (il seme) come il muro di supporto primario
                    // anche se potrebbe esserlo se il seme fosse esso stesso un muro valido.
                    BlockPos potentialWallPos = targetPos.relative(supportDirection);

                    // Il muro non può essere il seme stesso (a meno che non sia un log/mattone ecc., il che è impossibile)
                    // E non può essere la posizione target stessa.
                    if(potentialWallPos.equals(pPos) || potentialWallPos.equals(targetPos)) {
                        continue;
                    }

                    BlockState potentialWallState = pLevel.getBlockState(potentialWallPos);

                    // Il muro deve essere un muro valido E solido verso la posizione target dell'edera
                    if (isWall(potentialWallState) && potentialWallState.isFaceSturdy(pLevel, potentialWallPos, supportDirection.getOpposite())) {
                        foundWallSupport = true;
                        wallPos = potentialWallPos; // Salva dove è il muro
                        wallState = potentialWallState; // Salva che muro è
                        //TalesOfFolklore.LOGGER.debug("   -> Found valid wall support for {} at {} ({})", targetPos, wallPos, wallState.getBlock().getDescriptionId());
                        break; // Trovato un muro di supporto, basta cercare
                    }
                }

                // Se abbiamo trovato un muro di supporto valido per targetPos...
                if (foundWallSupport) {
                    BlockState ivyState = ModBlocks.WALL_IVY.get().defaultBlockState();
                    pLevel.setBlock(targetPos, ivyState, 3); // Piazza l'edera!
                    // Metti 'return;' qui se vuoi SOLO 1 crescita per ogni randomTick.
                    // Lascialo commentato se vuoi che provi a riempire tutti i lati validi disponibili.
                    // return;
                } else {
                    //TalesOfFolklore.LOGGER.debug("   -> FAILED: No valid wall support found adjacent to target {}", targetPos);
                }
            } else {
                // String blockName = pLevel.getBlockState(targetPos).getBlock().getDescriptionId();
                // TalesOfFolklore.LOGGER.debug("   -> Target {} is NOT Air ({}), skipping.", targetPos, blockName);
            }
        }
    }
}
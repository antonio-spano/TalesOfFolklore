package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock; // Keep this for property references
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition; // Import this
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.Set;

public class ModWallIvySeed extends Block {

    // These are fine as references
    public static final BooleanProperty NORTH = VineBlock.NORTH;
    public static final BooleanProperty EAST = VineBlock.EAST;
    public static final BooleanProperty SOUTH = VineBlock.SOUTH;
    public static final BooleanProperty WEST = VineBlock.WEST;
    // The 'UP' property is also commonly used by vines if they can be on ceilings,
    // but your current logic seems focused on horizontal attachment.
    // If you want it to be exactly like VineBlock, you might also need:
    // public static final BooleanProperty UP = VineBlock.UP;


    protected static final double THICKNESS = 1.0D;
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, THICKNESS, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(16.0D - THICKNESS, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 16.0D - THICKNESS, 16.0D, 16.0D, 16.0D);
    // You'll also need an UP_AABB if you decide to use the UP property for ceiling vines
    // protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, THICKNESS, 16.0D); // Example for UP

    private static final Set<Block> VALID_WALL_BLOCKS = Set.of(
            Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
            Blocks.BRICKS,
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.DEEPSLATE,
            Blocks.DEEPSLATE_BRICKS
    );

    private static final Set<Block> VALID_GROUND_OR_BRICK_BLOCKS = Set.of(
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.PODZOL,
            Blocks.COARSE_DIRT,
            Blocks.ROOTED_DIRT,
            Blocks.MOSS_BLOCK,
            Blocks.BRICKS
    );

    public ModWallIvySeed(Properties pProperties) {
        super(pProperties.sound(SoundType.GRASS).randomTicks().noOcclusion().noCollission());
        // Set default state for the properties you define
        // This is important to prevent null pointer exceptions if a property isn't set.
        this.registerDefaultState(this.stateDefinition.any()
                        .setValue(NORTH, Boolean.valueOf(false))
                        .setValue(EAST, Boolean.valueOf(false))
                        .setValue(SOUTH, Boolean.valueOf(false))
                        .setValue(WEST, Boolean.valueOf(false))
                // .setValue(UP, Boolean.valueOf(false)) // If you add UP
        );
    }

    // --- THIS IS THE CRITICAL FIX ---
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST); // Add UP here if you intend to use it
        // If you add UP: builder.add(NORTH, EAST, SOUTH, WEST, UP);
    }
    // --- END CRITICAL FIX ---


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        // Check if the property exists before trying to get its value, though with the fix above this is less critical for crashing
        // but good practice if you weren't sure if a property was always present.
        if (pState.hasProperty(SOUTH) && pState.getValue(SOUTH)) { shape = Shapes.or(shape, NORTH_AABB); } // Note: SOUTH true means vine on SOUTH face, shape extends NORTH
        if (pState.hasProperty(NORTH) && pState.getValue(NORTH)) { shape = Shapes.or(shape, SOUTH_AABB); } // Note: NORTH true means vine on NORTH face, shape extends SOUTH
        if (pState.hasProperty(WEST) && pState.getValue(WEST)) { shape = Shapes.or(shape, EAST_AABB); }   // Note: WEST true means vine on WEST face, shape extends EAST
        if (pState.hasProperty(EAST) && pState.getValue(EAST)) { shape = Shapes.or(shape, WEST_AABB); }   // Note: EAST true means vine on EAST face, shape extends WEST
        // if (pState.hasProperty(UP) && pState.getValue(UP)) { shape = Shapes.or(shape, UP_AABB); } // If you add UP

        return shape.isEmpty() ? Shapes.block() : shape;
    }


    public static boolean isWall(BlockState state) {
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS)) {
            return true;
        }
        return VALID_WALL_BLOCKS.contains(state.getBlock());
    }

    public static boolean isValidGroundOrBrick(BlockState groundState) {
        return VALID_GROUND_OR_BRICK_BLOCKS.contains(groundState.getBlock());
    }

    private static boolean canAttachTo(LevelReader pLevel, BlockPos pPos, Direction pDirection) {
        if (!pDirection.getAxis().isHorizontal()) { // Also allow UP if you want ceiling vines
            // if (pDirection != Direction.UP && !pDirection.getAxis().isHorizontal()) return false; // If UP is allowed
            return false;
        }
        BlockPos neighborPos = pPos.relative(pDirection);
        BlockState neighborState = pLevel.getBlockState(neighborPos);
        return isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, pDirection.getOpposite());
    }

    // This can be removed if canAttachTo is sufficient, or keep if you explicitly need it for getShape logic.
    // For now, I'll assume canAttachTo is what you primarily use and getShape will rely on the state's properties.
    /*
    private static boolean canAttachToShape(BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        if (!pDirection.getAxis().isHorizontal()) {
            return false;
        }
        BlockPos neighborPos = pPos.relative(pDirection);
        BlockState neighborState = pLevel.getBlockState(neighborPos);
        return isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, pDirection.getOpposite());
    }
    */

    private static boolean hasAdjacentWallSupport(LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (canAttachTo(pLevel, pPos, direction)) {
                return true;
            }
        }
        // If you allow UP vines:
        // if (canAttachTo(pLevel, pPos, Direction.UP)) return true;
        return false;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        BlockState belowState = pLevel.getBlockState(belowPos);

        boolean groundValid = isValidGroundOrBrick(belowState) && belowState.isFaceSturdy(pLevel, belowPos, Direction.UP);
        if (!groundValid) {
            return false;
        }
        // The actual state of the vine seed (which faces it's attached to)
        // will be determined by updateShape. canSurvive here mainly checks if the *spot* is valid.
        // For a vine-like block, it needs at least one face it *could* attach to,
        // or be supported from below (which your groundValid checks).
        // The actual attachment state is set in updateShape.
        return hasAdjacentWallSupport(pLevel, pPos); // Simplified: if it can attach to *any* side, it can survive.
    }

    // updateShape is crucial for setting the NORTH, SOUTH, EAST, WEST properties correctly.
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!this.canSurvive(pState, pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        // This is where you set the properties based on adjacent blocks.
        // This logic is similar to what VineBlock does.
        boolean north = canAttachTo(pLevel, pCurrentPos, Direction.NORTH);
        boolean east = canAttachTo(pLevel, pCurrentPos, Direction.EAST);
        boolean south = canAttachTo(pLevel, pCurrentPos, Direction.SOUTH);
        boolean west = canAttachTo(pLevel, pCurrentPos, Direction.WEST);
        // boolean up = canAttachTo(pLevel, pCurrentPos, Direction.UP); // if you add UP

        // If no sides can be attached to, and it's not supported from below (though canSurvive should handle this),
        // it should break.
        if (!north && !east && !south && !west /* && !up */) { // add !up if applicable
            // Check canSurvive again as a fallback, especially if you allow it to be freestanding sometimes
            return this.canSurvive(pState, pLevel, pCurrentPos) ? pState : Blocks.AIR.defaultBlockState();
        }

        return pState
                .setValue(NORTH, Boolean.valueOf(north))
                .setValue(EAST, Boolean.valueOf(east))
                .setValue(SOUTH, Boolean.valueOf(south))
                .setValue(WEST, Boolean.valueOf(west));
        // .setValue(UP, Boolean.valueOf(up)); // if you add UP
    }


    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, false);
            return;
        }

        if (pRandom.nextInt(4) != 0) {
            return;
        }

        BlockPos posAbove1 = pPos.above();
        BlockPos posAbove2 = pPos.above(2);
        BlockState ivyBaseState = ModBlocks.WALL_IVY.get().defaultBlockState();

        if (pLevel.isEmptyBlock(posAbove1)) {
            BlockState actualIvyState1 = ModWallIvy.getStateBasedOnAdjacentWalls(pLevel, posAbove1, ivyBaseState);
            if (!actualIvyState1.isAir()) {
                pLevel.setBlock(posAbove1, actualIvyState1, 3);

                if (pLevel.isEmptyBlock(posAbove2)) {
                    BlockState actualIvyState2 = ModWallIvy.getStateBasedOnAdjacentWalls(pLevel, posAbove2, ivyBaseState);
                    if (!actualIvyState2.isAir() && pRandom.nextInt(2) == 0) {
                        pLevel.setBlock(posAbove2, actualIvyState2, 3);
                    }
                }
            }
        }
        else if (pLevel.getBlockState(posAbove1).is(ModBlocks.WALL_IVY.get())) {
            if (pLevel.isEmptyBlock(posAbove2)) {
                BlockState actualIvyState2 = ModWallIvy.getStateBasedOnAdjacentWalls(pLevel, posAbove2, ivyBaseState);
                if (!actualIvyState2.isAir() && pRandom.nextInt(2) == 0) {
                    pLevel.setBlock(posAbove2, actualIvyState2, 3);
                }
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelReader level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState defaultState = this.defaultBlockState(); // Inizia con lo stato di default (tutte le facce false)

        // Prima controlla se il blocco PUO' essere piazzato in quella posizione
        // La tua logica canSurvive è importante qui.
        // Se canSurvive restituisce false (es. piazzato a mezz'aria senza terreno sotto valido),
        // allora getStateForPlacement dovrebbe restituire null per impedire il piazzamento.
        if (!this.canSurvive(defaultState, level, blockpos)) {
            // Potresti voler restituire null se le condizioni di base (es. terreno sotto) non sono soddisfatte
            // O se non c'è NESSUN supporto laterale.
            // Per ora, se canSurvive (che controlla terreno valido + almeno un supporto laterale) fallisce, non piazzare.
            return null;
        }

        // Ora calcola lo stato REALE basato sui muri adiacenti
        boolean north = canAttachTo(level, blockpos, Direction.NORTH);
        boolean east = canAttachTo(level, blockpos, Direction.EAST);
        boolean south = canAttachTo(level, blockpos, Direction.SOUTH);
        boolean west = canAttachTo(level, blockpos, Direction.WEST);
        // boolean up = ALLOW_CEILING_PLACEMENT && canAttachToWall(level, blockpos, Direction.UP); // Se supporti il soffitto

        // Se ANCHE dopo aver controllato i muri, nessuno è valido, e canSurvive si basa solo sul terreno,
        // potresti voler comunque impedire il piazzamento o restituire uno stato "non attaccato".
        // Tuttavia, il tuo canSurvive attuale richiede già supporto laterale.

        return defaultState // Prendi lo stato di default...
                .setValue(NORTH, north) // ...e imposta i valori corretti
                .setValue(EAST, east)
                .setValue(SOUTH, south)
                .setValue(WEST, west);
        // .setValue(UP, up); // Se supporti il soffitto
    }
}
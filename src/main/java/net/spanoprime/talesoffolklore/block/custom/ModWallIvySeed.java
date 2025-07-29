package net.spanoprime.talesoffolklore.block.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.Map;

public class ModWallIvySeed extends Block {

    public static final BooleanProperty NORTH = VineBlock.NORTH;
    public static final BooleanProperty EAST = VineBlock.EAST;
    public static final BooleanProperty SOUTH = VineBlock.SOUTH;
    public static final BooleanProperty WEST = VineBlock.WEST;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.<Direction, BooleanProperty>builder()
            .put(Direction.NORTH, NORTH)
            .put(Direction.EAST, EAST)
            .put(Direction.SOUTH, SOUTH)
            .put(Direction.WEST, WEST)
            .build();

    protected static final double THICKNESS = 1.0D;
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, THICKNESS, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(16.0D - THICKNESS, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 16.0D - THICKNESS, 16.0D, 16.0D, 16.0D);

    public ModWallIvySeed(Properties pProperties) {
        // --- FIX #2: RIMUOVI .noCollission() ---
        super(pProperties.sound(SoundType.GRASS).randomTicks().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    // --- FIX #1: Rendi getStateForPlacement "STUPIDO" E PRECISO ---
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelReader level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        // Controlla prima il requisito fondamentale: il terreno sotto.
        if (!isValidGroundOrBrick(level.getBlockState(pos.below()))) {
            return null;
        }

        BlockState stateToPlace = this.defaultBlockState();
        boolean canBePlaced = false;

        // Itera sulle direzioni in cui il giocatore sta guardando.
        // Questo è più affidabile che usare solo la faccia cliccata.
        for (Direction direction : pContext.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                // Controlla se c'è un muro valido a cui attaccarsi.
                if (canAttachTo(level, pos, direction)) {
                    // Se sì, imposta SOLO QUELLA faccia e esci dal loop.
                    stateToPlace = stateToPlace.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                    canBePlaced = true;
                    break;
                }
            }
        }

        // Se dopo aver controllato non abbiamo trovato NESSUN muro, il piazzamento è invalido.
        if (!canBePlaced) {
            return null;
        }

        return stateToPlace;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        // never let another block replace me
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        if (pState.getValue(SOUTH)) { shape = Shapes.or(shape, NORTH_AABB); }
        if (pState.getValue(NORTH)) { shape = Shapes.or(shape, SOUTH_AABB); }
        if (pState.getValue(WEST)) { shape = Shapes.or(shape, EAST_AABB); }
        if (pState.getValue(EAST)) { shape = Shapes.or(shape, WEST_AABB); }
        return shape.isEmpty() ? Shapes.block() : shape;
    }

    public static boolean isWall(BlockState state) {
        Block block = state.getBlock();
        return state.is(BlockTags.LOGS) ||
                state.is(BlockTags.PLANKS) ||
                block == Blocks.STONE_BRICKS ||
                block == Blocks.MOSSY_STONE_BRICKS ||
                block == Blocks.CRACKED_STONE_BRICKS ||
                block == Blocks.CHISELED_STONE_BRICKS ||
                block == Blocks.BRICKS ||
                block == Blocks.STONE ||
                block == Blocks.COBBLESTONE ||
                block == Blocks.DEEPSLATE ||
                block == Blocks.DEEPSLATE_BRICKS;
    }

    public static boolean isValidGroundOrBrick(BlockState groundState) {
        Block block = groundState.getBlock();
        return block == Blocks.DIRT ||
                block == Blocks.GRASS_BLOCK ||
                block == Blocks.PODZOL ||
                block == Blocks.COARSE_DIRT ||
                block == Blocks.ROOTED_DIRT ||
                block == Blocks.MOSS_BLOCK ||
                block == Blocks.BRICKS ||
                block == ModBlocks.DAMP_DIRT.get() ||
                block == ModBlocks.DAMP_PODZOL.get() ||
                block == ModBlocks.DAMP_GRASS_BLOCK.get() ||
                block == ModBlocks.DAMP_COARSE_DIRT.get();
    }

    private static boolean canAttachTo(LevelReader pLevel, BlockPos pPos, Direction pDirection) {
        if (!pDirection.getAxis().isHorizontal()) {
            return false;
        }
        BlockPos neighborPos = pPos.relative(pDirection);
        BlockState neighborState = pLevel.getBlockState(neighborPos);
        return isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, pDirection.getOpposite());
    }

    private static boolean hasAdjacentWallSupport(LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (canAttachTo(pLevel, pPos, direction)) {
                return true;
            }
        }
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
        return hasAdjacentWallSupport(pLevel, pPos);
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

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        BlockState newState = this.defaultBlockState();
        for(Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (pState.getValue(entry.getValue())) {
                Direction newDirection = pRotation.rotate(entry.getKey());
                newState = newState.setValue(PROPERTY_BY_DIRECTION.get(newDirection), true);
            }
        }
        return newState;
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        BlockState newState = this.defaultBlockState();
        for(Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (pState.getValue(entry.getValue())) {
                Direction newDirection = pMirror.mirror(entry.getKey());
                newState = newState.setValue(PROPERTY_BY_DIRECTION.get(newDirection), true);
            }
        }
        return newState;
    }
}
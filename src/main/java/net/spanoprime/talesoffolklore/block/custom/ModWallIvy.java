package net.spanoprime.talesoffolklore.block.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.Map;

public class ModWallIvy extends Block {

    public static final BooleanProperty NORTH = VineBlock.NORTH;
    public static final BooleanProperty EAST = VineBlock.EAST;
    public static final BooleanProperty SOUTH = VineBlock.SOUTH;
    public static final BooleanProperty WEST = VineBlock.WEST;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = VineBlock.PROPERTY_BY_DIRECTION.entrySet().stream()
            .filter(entry -> entry.getKey().getAxis().isHorizontal())
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    protected static final double THICKNESS = 1.0D;
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, THICKNESS, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(16.0D - THICKNESS, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 16.0D - THICKNESS, 16.0D, 16.0D, 16.0D);

    public ModWallIvy(Properties pProperties) {
        // --- FIX #2: RIMUOVI .noCollission() ---
        super(pProperties.noOcclusion().instabreak().sound(SoundType.VINE));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST);
    }

    // --- FIX #1: Rendi getStateForPlacement "STUPIDO" E PRECISO ---
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelReader level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        BlockState stateToPlace = this.defaultBlockState();
        boolean canBePlaced = false;

        for (Direction direction : pContext.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                if (canAttachTo(level, pos, direction)) {
                    stateToPlace = stateToPlace.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                    canBePlaced = true;
                    break;
                }
            }
        }

        if (!canBePlaced) {
            return null;
        }
        return stateToPlace;
    }
    // --- FINE FIX #1 ---


    // [ ... IL RESTO DEL TUO CODICE, CHE È GIUSTO, VA QUI ... ]
    // ... Aggiungi i metodi rotate e mirror per consistenza ...

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        if (pState.getValue(SOUTH)) { shape = Shapes.or(shape, NORTH_AABB); }
        if (pState.getValue(NORTH)) { shape = Shapes.or(shape, SOUTH_AABB); }
        if (pState.getValue(WEST)) { shape = Shapes.or(shape, EAST_AABB); }
        if (pState.getValue(EAST)) { shape = Shapes.or(shape, WEST_AABB); }
        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        // never let another block replace me
        return false;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return !getStateBasedOnAdjacentWalls(pLevel, pPos, this.defaultBlockState()).isAir();
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!this.canSurvive(pState, pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    private static boolean canAttachTo(LevelReader pLevel, BlockPos pPos, Direction pDirection) {
        if (!PROPERTY_BY_DIRECTION.containsKey(pDirection)) {
            return false;
        }
        BlockPos neighborPos = pPos.relative(pDirection);
        BlockState neighborState = pLevel.getBlockState(neighborPos);
        return ModWallIvySeed.isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, pDirection.getOpposite());
    }

    public static BlockState getStateBasedOnAdjacentWalls(LevelReader pLevel, BlockPos pPos, BlockState ivyBaseState) {
        BlockState state = ivyBaseState;
        boolean foundSupport = false;
        state = state.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (canAttachTo(pLevel, pPos, direction)) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                if (property != null) {
                    state = state.setValue(property, true);
                    foundSupport = true;
                }
            }
        }
        return foundSupport ? state : Blocks.AIR.defaultBlockState();
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
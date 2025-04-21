package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import javax.annotation.Nullable;

public class ModWallMossBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SHAPE_EAST  = Block.box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SHAPE_WEST  = Block.box(15, 0, 0, 16, 16, 16);

    public ModWallMossBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); // Impostiamo la direzione predefinita
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST  -> SHAPE_EAST;
            case WEST  -> SHAPE_WEST;
            default -> Shapes.empty(); // evita problemi con UP/DOWN
        };
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        Direction facing = pState.getValue(FACING);
        // Se il blocco a cui siamo attaccati viene rimosso o non è più solido, autodistruggiti
        if (pDirection == facing.getOpposite() && !canSurvive(pState, pLevel, pPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction dir : context.getNearestLookingDirections()) {
            if (dir.getAxis().isHorizontal()) {
                BlockPos neighborPos = context.getClickedPos().relative(dir.getOpposite());
                BlockState neighborState = context.getLevel().getBlockState(neighborPos);
                if (neighborState.isFaceSturdy(context.getLevel(), neighborPos, dir)) {
                    return this.defaultBlockState().setValue(FACING, dir);
                }
            }
        }
        return null; // non si piazza se non trova una parete adatta
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction dir = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(dir.getOpposite());
        BlockState attachedState = level.getBlockState(attachedPos);

        // Controlla se il blocco a cui è attaccato è un log (inclusi quelli di Virginia Pine)
        if (attachedState.is(Blocks.OAK_LOG) || attachedState.is(Blocks.SPRUCE_LOG) || attachedState.is(Blocks.BIRCH_LOG) ||
                attachedState.is(Blocks.JUNGLE_LOG) || attachedState.is(Blocks.ACACIA_LOG) || attachedState.is(Blocks.DARK_OAK_LOG) ||
                attachedState.is(Blocks.OAK_WOOD) || attachedState.is(Blocks.SPRUCE_WOOD) ||
                attachedState.is(Blocks.BIRCH_WOOD) || attachedState.is(Blocks.JUNGLE_WOOD) ||
                attachedState.is(Blocks.ACACIA_WOOD) || attachedState.is(Blocks.DARK_OAK_WOOD) ||
                attachedState.is(ModBlocks.VIRGINIA_PINE_LOG.get()) || attachedState.is(ModBlocks.VIRGINIA_PINE_WOOD.get())) {
            return true;
        }

        return false; // Il blocco si attacca solo se è un log
    }


    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
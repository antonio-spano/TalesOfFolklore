package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class ModLeavesBlock extends LeavesBlock
{
    public ModLeavesBlock(Properties pProperties) {
        super(pProperties);
    }
/*
    private int getDistanceAt(BlockState state) {
        if (state.getBlock() instanceof LeavesBlock) {
            return state.getValue(DISTANCE);
        } else if (state.is(net.minecraft.tags.BlockTags.LOGS)) {
            return 0;
        } else {
            return 7;
        }
    } */

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 30;
    }
/*
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        int newDistance = getDistanceAt(neighborState) + 1;
        if (newDistance != 1 || state.getValue(DISTANCE) != newDistance) {
            level.scheduleTick(currentPos, this, 1);
        }

        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int newDistance = 7;
        for (Direction direction : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(direction));
            newDistance = Math.min(newDistance, getDistanceAt(neighborState) + 1);
            if (newDistance == 1) break;
        }

        if (newDistance != state.getValue(DISTANCE)) {
            level.setBlock(pos, state.setValue(DISTANCE, newDistance), 2);
        }
    } */
/*
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISTANCE, PERSISTENT);
    } */
/*
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        int newDistance = getDistanceAt(neighborState) + 1;
        if (newDistance != 1 || state.getValue(DISTANCE) != newDistance) {
            level.scheduleTick(currentPos, this, 1);
        }

        return state;
    } */
}

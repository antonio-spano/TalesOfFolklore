package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ModLadderBlock extends LadderBlock
{
    public ModLadderBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        // Permette al blocco di "sopravvivere" anche senza blocco dietro
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Imposta la direzione in base a dove guarda il giocatore
        Direction dir = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, dir)
                .setValue(WATERLOGGED, false); // o true se vuoi gestire l'acqua
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        // Impedisce che si rompa da solo se manca il blocco di supporto
        // (normalmente chiama level.destroyBlock se non può sopravvivere)
    }
}

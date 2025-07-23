package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spanoprime.talesoffolklore.entity.ModSpriteBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ModSpriteBlock extends BaseEntityBlock {

    // Una VoxelShape vuota, così il blocco non ha collisione. Puoi camminarci attraverso.
    protected static final VoxelShape SHAPE = net.minecraft.world.level.block.Block.box(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public ModSpriteBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    // Questo è FONDAMENTALE. Dice a Minecraft di non provare a disegnare il cubo del blocco.
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    // Questo collega il blocco al suo BlockEntity.
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModSpriteBlockEntity(pPos, pState);
    }
}
package net.spanoprime.talesoffolklore.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModSpriteBlockEntity extends BlockEntity {
    public ModSpriteBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MOD_SPRITE.get(), pPos, pBlockState);
    }
}
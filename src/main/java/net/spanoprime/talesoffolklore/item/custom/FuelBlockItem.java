package net.spanoprime.talesoffolklore.item.custom;

import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class FuelBlockItem extends BlockItem
{
    private int burnTime = 0;

    public FuelBlockItem(Block block, Properties pProperties, int burnTime) {
        super(block, pProperties);
        this.burnTime = burnTime;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }
}

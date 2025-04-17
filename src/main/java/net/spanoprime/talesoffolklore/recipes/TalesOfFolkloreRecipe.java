package net.spanoprime.talesoffolklore.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class TalesOfFolkloreRecipe extends ShapedRecipe {
    public TalesOfFolkloreRecipe(ResourceLocation pId, String pGroup, CraftingBookCategory pCategory, int pWidth, int pHeight, NonNullList<Ingredient> pRecipeItems, ItemStack pResult, boolean pShowNotification) {
        super(pId, pGroup, pCategory, pWidth, pHeight, pRecipeItems, pResult, pShowNotification);
    }
}

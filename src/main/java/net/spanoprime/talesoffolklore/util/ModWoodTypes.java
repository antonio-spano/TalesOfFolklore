package net.spanoprime.talesoffolklore.util;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModWoodTypes
{
    public static final WoodType VIRGINIA_PINE = WoodType.register(new WoodType(TalesOfFolklore.MOD_ID + ":virginia_pine",
            BlockSetType.ACACIA));
}

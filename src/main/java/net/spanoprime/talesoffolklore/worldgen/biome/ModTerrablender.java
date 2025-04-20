package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.resources.ResourceLocation;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import terrablender.api.Regions;

public class ModTerrablender
{
    public static void registerBiomes()
    {
        Regions.register(new ModOverworldRegion(ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "overworld"), 5));
    }
}

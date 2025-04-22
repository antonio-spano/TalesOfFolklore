package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.resources.ResourceLocation;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import terrablender.api.Regions;

public class ModTerrablender
{
    public static void registerBiomes()
    {
        // Torniamo a un peso basso, come 1 o 2. Con parametri più specifici,
        // non c'è bisogno di un peso alto. Prova con 2.
        Regions.register(new ModOverworldRegion(ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "overworld"), 13));
        // ^--- Peso riportato a 2
    }
}
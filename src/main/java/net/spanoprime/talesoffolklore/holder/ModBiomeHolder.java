package net.spanoprime.talesoffolklore.holder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID)
public class ModBiomeHolder {
    public static Holder<Biome> APPALACHIAN_HOLDER;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent evt) {
        // prendo il registryAccess del server
        MinecraftServer server = evt.getServer();
        // estraggo il dynamic registry dei biomi
        Holder<Biome> h =
                server
                        .registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getHolder(ModBiomes.APPALACHIAN_FOREST)
                        .orElseThrow(() -> new IllegalStateException("Non ho trovato APPALACHIAN_FOREST nel registry!"));
        APPALACHIAN_HOLDER = h;
    }
}

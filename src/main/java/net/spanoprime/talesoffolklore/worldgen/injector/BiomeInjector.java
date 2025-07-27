package net.spanoprime.talesoffolklore.worldgen.injector;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;

import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID)
public class BiomeInjector {
    public static Holder<Biome> APPALACHIAN_FOREST_HOLDER = null;

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<Biome> biomeRegistry = event.getServer().registryAccess().registryOrThrow(Registries.BIOME);
        APPALACHIAN_FOREST_HOLDER = biomeRegistry.getHolderOrThrow(ModBiomes.APPALACHIAN_FOREST);

        System.out.println("[TALES OF FOLKLORE] BIOME INJECTOR ARMED. Holder captured: " + APPALACHIAN_FOREST_HOLDER.unwrapKey());
    }
}
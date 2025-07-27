package net.spanoprime.talesoffolklore.worldgen.injector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
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
    public static int minDistance = 4000;
    public static int maxDistanceOffset = 2000;
    public static BlockPos appalachianCenter = null;

    public static BlockPos findLandCenter(ServerLevel level, int maxTries, int minDistance_, int maxDistanceOffset_) {
        for (int tries = 0; tries < maxTries; tries++) {
            int distanceOffset = (int) (Math.random() * maxDistanceOffset_);
            float angle = (float) (Math.random() * (2 * Math.PI));

            int centerX = (int) Math.cos(angle) * (minDistance_ + distanceOffset);
            int centerZ = (int) Math.sin(angle) * (minDistance_ + distanceOffset);

            BlockPos pos = new BlockPos(centerX, level.getSeaLevel(), centerZ);
            Holder<Biome> biome = level.getBiome(pos);

            if (!isAquaticBiome(biome) && !isBeachBiome(biome) && !isExcludedBiome(biome)) {
                return pos;
            }
        }
        // fallback se non trovato (raro)
        return new BlockPos(0, level.getSeaLevel(), 0);
    }

    public static boolean isAquaticBiome(Holder<Biome> biome)
    {
        return biome.containsTag(BiomeTags.IS_OCEAN)
                || biome.containsTag(BiomeTags.IS_DEEP_OCEAN)
                || biome.containsTag(BiomeTags.IS_RIVER);
    }

    public static boolean isBeachBiome(Holder<Biome> biome) { return biome.containsTag(BiomeTags.IS_BEACH); }

    public static boolean isExcludedBiome(Holder<Biome> biome) { return biome.containsTag(BiomeTags.IS_MOUNTAIN); }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<Biome> biomeRegistry = event.getServer().registryAccess().registryOrThrow(Registries.BIOME);
        APPALACHIAN_FOREST_HOLDER = biomeRegistry.getHolderOrThrow(ModBiomes.APPALACHIAN_FOREST);

        ServerLevel overworld = event.getServer().overworld();
        if (overworld != null) {
            RandomSource random = overworld.getRandom();
            appalachianCenter = findLandCenter(overworld, 150, minDistance, maxDistanceOffset);
            System.out.println("[TALES OF FOLKLORE] Land center for Appalachian Forest: " + appalachianCenter);
        }

        System.out.println("[TALES OF FOLKLORE] BIOME INJECTOR ARMED. Holder captured: " + APPALACHIAN_FOREST_HOLDER.unwrapKey());
    }
}
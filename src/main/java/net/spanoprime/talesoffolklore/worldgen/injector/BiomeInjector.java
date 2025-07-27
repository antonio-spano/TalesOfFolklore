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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import net.spanoprime.talesoffolklore.worldgen.biome.ModLandFinder;

import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID)
public class BiomeInjector {
    public static Holder<Biome> APPALACHIAN_FOREST_HOLDER = null;
    public static int minDistance = 4000;
    public static int maxDistanceOffset = 2000;
    public static BlockPos appalachianCenter = BlockPos.ZERO;

    public static BlockPos findLandCenter(ServerLevel level, int yQuart, int minDistance_, int maxDistanceOffset_) {
        BlockPos pos = BlockPos.ZERO;
        int centerX, centerZ;
        do {
            int distanceOffset = (int) (Math.random() * maxDistanceOffset_);
            float angle = (float) (Math.random() * (2 * Math.PI));

            centerX = (int) (Math.cos(angle) * (minDistance_ + distanceOffset));
            centerZ = (int) (Math.sin(angle) * (minDistance_ + distanceOffset));

            pos = new BlockPos(centerX, yQuart, centerZ);
        } while (ModLandFinder.isLand(level, centerX, centerZ));

        return pos;
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

        System.out.println("[TALES OF FOLKLORE] BIOME INJECTOR ARMED. Holder captured: " + APPALACHIAN_FOREST_HOLDER.unwrapKey());
    }
}
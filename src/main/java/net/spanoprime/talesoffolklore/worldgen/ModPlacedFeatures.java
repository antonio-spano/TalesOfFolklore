package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> VIRGINIA_PINE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, "virginia_pine_placed"));

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        System.out.println("ModConfiguredFeatures: Bootstrapping started 2!");
/*
        register(context, VIRGINIA_PINE_PLACED_KEY, ModConfiguredFeatures.VIRGINIA_PINE_KEY, List.of(
                PlacementUtils.filteredByBlockSurvival(Blocks.GRASS_BLOCK), // Makes sure they spawn on grass-like blocks
                PlacementUtils.HEIGHTMAP, // Spawns using heightmaps
                BiomeFilter.biome() // Restricts placement to the correct biome(s)
        )); */
        System.out.println("ModConfiguredFeatures: Bootstrapping completed 2!");
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
                                 List<PlacementModifier> placementModifiers) {
        context.register(key, new PlacedFeature(
                context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredFeatureKey),
                placementModifiers
        ));
    }
}

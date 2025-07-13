package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> VIRGINIA_PINE_PLACED_KEY = registerKey("virginia_pine_placed");
    public static final ResourceKey<PlacedFeature> RIVERBANK_ROCK_PLACED_KEY = registerKey("riverbank_rock_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);


    /*
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get())); */
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get()));

        ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation(TalesOfFolklore.MOD_ID, "virginia_pine_placed"));

        register(context, RIVERBANK_ROCK_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RIVERBANK_ROCK_KEY),
                List.of(CountPlacement.of(6),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.anyOf(
                                        BlockPredicate.matchesBlocks(new BlockPos(1, -1, 0), Blocks.WATER),
                                        BlockPredicate.matchesBlocks(new BlockPos(-1, -1, 0), Blocks.WATER),
                                        BlockPredicate.matchesBlocks(new BlockPos(0, -1, 1), Blocks.WATER),
                                        BlockPredicate.matchesBlocks(new BlockPos(0, -1, -1), Blocks.WATER)
                                )
                        ),
                        BiomeFilter.biome())
        );
    }

    public static ResourceKey<PlacedFeature> registerKey(String name)
    {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }
/*
    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
                                 List<PlacementModifier> placementModifiers) {
        context.register(key, new PlacedFeature(
                context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredFeatureKey),
                placementModifiers
        ));
    } */

    public static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                Holder<ConfiguredFeature<?, ?>> configuration,
                                List<PlacementModifier> modifiers)
    {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}

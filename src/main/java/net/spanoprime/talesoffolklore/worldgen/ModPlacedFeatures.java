package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {

    // Chiave per l'albero
    public static final ResourceKey<PlacedFeature> VIRGINIA_PINE_PLACED_KEY = registerKey("virginia_pine_placed");
    public static final ResourceKey<PlacedFeature> STREAM_CARVER_PLACED_KEY = registerKey("stream_carver_placed");
    public static final ResourceKey<PlacedFeature> RED_FUNGUS_PLACED_KEY = registerKey("red_fungus_placed");
    public static final ResourceKey<PlacedFeature> FERN_PLACED_KEY = registerKey("fern_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // --- VIRGINIA PINE ---
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get()));

        register(context, STREAM_CARVER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STREAM_CARVER_KEY),
                List.of(
                        // Prova a generare un torrente ogni 10 chunk. Modifica questo valore per frequenza.
                        RarityFilter.onAverageOnceEvery(1),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        // Sostituisci la tua riga con questa:
        register(context, RED_FUNGUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RED_FUNGUS_KEY),
                List.of(
                        // 1. RarityFilter: Tenta di piazzare una patch in media UNA volta ogni 32 chunk.
                        //    Questo garantisce che la maggior parte dei chunk non abbia funghi. Aumenta per renderli più rari.
                        RarityFilter.onAverageOnceEvery(2),

                        // 2. I soliti modifier per piazzare la patch in un punto a caso sulla superficie.
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, FERN_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FERN_KEY),
                List.of(
                        // 1. RarityFilter: Tenta di piazzare una patch in media UNA volta ogni 32 chunk.
                        //    Questo garantisce che la maggior parte dei chunk non abbia funghi. Aumenta per renderli più rari.
                        RarityFilter.onAverageOnceEvery(1),

                        // 2. I soliti modifier per piazzare la patch in un punto a caso sulla superficie.
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }

    public static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                Holder<ConfiguredFeature<?, ?>> configuration,
                                List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
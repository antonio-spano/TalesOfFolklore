package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {

    // Chiave per l'albero
    public static final ResourceKey<PlacedFeature> VIRGINIA_PINE_PLACED_KEY = registerKey("virginia_pine_placed");

    // Chiave per il letto del torrente
    public static final ResourceKey<PlacedFeature> SHALLOW_RIVERBED_PLACED_KEY = registerKey("shallow_riverbed_placed");

    public static final ResourceKey<PlacedFeature> STREAM_CARVER_PLACED_KEY = registerKey("stream_carver_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // --- VIRGINIA PINE ---
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get()));

        // --- APPALACHIAN STREAMBED ---
        register(context, SHALLOW_RIVERBED_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SHALLOW_RIVERBED_KEY),
                List.of(
                        InSquarePlacement.spread(), // Esegui nel chunk
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, // Allineati alla superficie solida
                        BiomeFilter.biome() // Solo nel bioma corretto (verrà applicato in ModBiomes)
                ));

        register(context, STREAM_CARVER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STREAM_CARVER_KEY),
                List.of(
                        // Prova a generare un torrente ogni 10 chunk. Modifica questo valore per frequenza.
                        RarityFilter.onAverageOnceEvery(10),
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
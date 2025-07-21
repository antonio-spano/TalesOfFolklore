package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
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

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // --- VIRGINIA PINE ---
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get()));

        register(context, STREAM_CARVER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STREAM_CARVER_KEY),
                List.of(
                        // Prova a generare un torrente ogni 10 chunk. Modifica questo valore per frequenza.
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        // Sostituisci la tua riga con questa:
        register(context, RED_FUNGUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RED_FUNGUS_KEY),
                // Invece di usare un pacchetto per l'erba, costruiamo le regole a mano:
                List.of(
                        // Regola 1: Frequenza. Quante "chiazze" per chunk.
                        // Puoi usare CountPlacement.of(X) per un numero fisso,
                        // o il tuo countExtra per un numero variabile. Usiamo il tuo:
                        PlacementUtils.countExtra(2, .1f, 1),

                        // Regola 2: Spargili a caso nel chunk.
                        InSquarePlacement.spread(),

                        // Regola 3: Piazzali sulla superficie del mondo.
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,

                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),

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
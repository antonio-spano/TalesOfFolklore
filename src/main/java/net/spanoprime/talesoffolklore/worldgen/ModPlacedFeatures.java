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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

import java.util.List;

public class ModPlacedFeatures {

    // Chiave per l'albero
    public static final ResourceKey<PlacedFeature> VIRGINIA_PINE_PLACED_KEY = registerKey("virginia_pine_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // --- VIRGINIA PINE ---
        // Questa è la riga che probabilmente mancava e che causava il crash.
        // Si assicura che VIRGINIA_PINE_KEY venga usato.
        register(context, VIRGINIA_PINE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VIRGINIA_PINE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, .1f, 2),
                        ModBlocks.VIRGINIA_PINE_SAPLING.get()));

        // --- RIVERBANK ROCKS ---
        // Numero di tentativi aumentato per ottenere una linea densa e continua
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
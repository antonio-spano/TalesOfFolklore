package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallMossDecorator;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        System.out.println("ModConfiguredFeatures: Bootstrapping started...");
        /*
            context.register(
                    VIRGINIA_PINE_KEY,
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LOG.get()),
                                    new StraightTrunkPlacer(5, 2, 0),
                                    BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LEAVES.get()),
                                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                                    new TwoLayersFeatureSize(1, 0, 1))
                                    .build()
                    )
            ); */

        TreeConfiguration config = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LOG.get()), // Trunk block
                new StraightTrunkPlacer(5, 2, 0), // Trunk shape
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LEAVES.get()), // Leaves block
                new MegaPineFoliagePlacer(
                        ConstantInt.of(0), // foliage offset (di solito 0)
                        ConstantInt.of(0), // foliage randomness (di solito 0)
                        ConstantInt.of(7)  // foliage radius, prova anche con 5-9
                ), // Foliage
                new TwoLayersFeatureSize(1, 0, 1) // Tree height layering
        )
                // Aggiunta del decoratore personalizzato (se vuoi usarlo)
                .decorators(List.of(new ModWallMossDecorator(.5f))) // oppure lascia vuoto se non vuoi usarlo
                .build();

        context.register(VIRGINIA_PINE_KEY, new ConfiguredFeature<>(Feature.TREE, config));

            System.out.println("ModConfiguredFeatures: Bootstrapping completed!");

    }

    private static void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                 ResourceKey<ConfiguredFeature<?, ?>> key,
                                 ConfiguredFeature<?, ?> feature) {
        context.register(key, feature);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}

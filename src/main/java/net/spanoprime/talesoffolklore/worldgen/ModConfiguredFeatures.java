package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallIvyDecorator;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallMossDecorator;

import java.util.ArrayList;
import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        System.out.println("ModConfiguredFeatures: Bootstrapping started...");

        List<TreeDecorator> decorators = new ArrayList<>();
        decorators.add(new ModWallMossDecorator(0.5f)); // Mantieni il tuo decoratore custom

        // Aggiungi il decoratore per il terreno (Mycelium + Coarse Dirt)
        decorators.add(new AlterGroundDecorator(
                // Usa un WeightedStateProvider per mischiare i blocchi
                new WeightedStateProvider(
                        SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.PODZOL.defaultBlockState(), 3) // Aggiunge Mycelium con peso 1 (50%)
                                .add(Blocks.COARSE_DIRT.defaultBlockState(), 1) // Aggiunge Coarse Dirt con peso 1 (50%)
                ) // Chiude il builder della lista pesata
        ));
        decorators.add(new ModWallIvyDecorator(0.25f));

        TreeConfiguration config = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LOG.get()), // Trunk block
                new StraightTrunkPlacer(21, 0, 3), // Trunk shape
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LEAVES.get()), // Leaves block
                new MegaPineFoliagePlacer(
                        ConstantInt.of(1), // foliage offset (di solito 0)
                        ConstantInt.of(0), // foliage randomness (di solito 0)
                        UniformInt.of(13, 17)  // foliage height
                ), // Foliage
                new TwoLayersFeatureSize(1, 0, 1) // Tree height layering
        )
                // Aggiunta del decoratore personalizzato (se vuoi usarlo)
                .decorators(decorators)
                // oppure lascia vuoto se non vuoi usarlo
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
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}

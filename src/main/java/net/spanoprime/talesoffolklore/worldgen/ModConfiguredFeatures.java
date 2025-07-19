package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallIvyDecorator;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallMossDecorator;
import net.spanoprime.talesoffolklore.worldgen.feature.ModFeatures;

import java.util.ArrayList;
import java.util.List;

public class ModConfiguredFeatures {

    // Chiave per l'albero
    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SHALLOW_RIVERBED_KEY = registerKey("shallow_riverbed");

    public static final ResourceKey<ConfiguredFeature<?, ?>> STREAM_CARVER_KEY = registerKey("stream_carver");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // --- VIRGINIA PINE ---
        List<TreeDecorator> decorators = new ArrayList<>();
        decorators.add(new ModWallMossDecorator(0.5f));
        decorators.add(new AlterGroundDecorator(
                new WeightedStateProvider(
                        SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.PODZOL.defaultBlockState(), 3)
                                .add(Blocks.COARSE_DIRT.defaultBlockState(), 1)
                )
        ));
        decorators.add(new ModWallIvyDecorator(0.25f));

        TreeConfiguration treeConfig = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LOG.get()),
                new StraightTrunkPlacer(21, 0, 3),
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LEAVES.get()),
                new MegaPineFoliagePlacer(
                        ConstantInt.of(1),
                        ConstantInt.of(0),
                        UniformInt.of(13, 17)
                ),
                new TwoLayersFeatureSize(1, 0, 1)
        ).decorators(decorators).build();

        register(context, VIRGINIA_PINE_KEY, Feature.TREE, treeConfig);

        register(context, SHALLOW_RIVERBED_KEY, ModFeatures.SHALLOW_RIVERBED.get(), new NoneFeatureConfiguration());

        register(context, STREAM_CARVER_KEY, ModFeatures.STREAM_CARVER.get(), new NoneFeatureConfiguration());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
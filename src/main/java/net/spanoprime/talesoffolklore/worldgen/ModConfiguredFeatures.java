package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
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
import net.spanoprime.talesoffolklore.worldgen.decorators.*;
import net.spanoprime.talesoffolklore.worldgen.feature.ModFeatures;

import java.util.ArrayList;
import java.util.List;

public class ModConfiguredFeatures {

    // Chiave per l'albero
    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STREAM_CARVER_KEY = registerKey("stream_carver");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_FUNGUS_KEY = registerKey("red_fungus");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // --- VIRGINIA PINE ---
        List<TreeDecorator> decorators = new ArrayList<>();
        decorators.add(new ModWallMossDecorator(0.5f));
        decorators.add(new AlterGroundDecorator(
                new WeightedStateProvider(
                        SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.PODZOL.defaultBlockState(), 1)
                                //.add(ModBlocks.LUSH_DIRT.get().defaultBlockState(), 2)
                                .add(Blocks.COARSE_DIRT.defaultBlockState(), 1)
                )
        ));
        decorators.add(new ModWallIvyDecorator(0.2f));
        decorators.add(new ModPineNeedlesDecorator(.3f));
        decorators.add(new ModYellowFungusDecorator(.05f));
        decorators.add(new ModUndergrowthDecorator(.3f));

        TreeConfiguration treeConfig = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LOG.get()),
                new StraightTrunkPlacer(22, 0, 5),
                BlockStateProvider.simple(ModBlocks.VIRGINIA_PINE_LEAVES.get()),
                new MegaPineFoliagePlacer(
                        ConstantInt.of(1),
                        ConstantInt.of(0),
                        UniformInt.of(13, 17)
                ),
                new TwoLayersFeatureSize(1, 0, 1)
        ).decorators(decorators).build();

        register(context, VIRGINIA_PINE_KEY, Feature.TREE, treeConfig);

        register(context, STREAM_CARVER_KEY, ModFeatures.STREAM_CARVER.get(), new NoneFeatureConfiguration());

        register(context, RED_FUNGUS_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        2, // Quanti tentativi per piazzare il fungo in una chiazza.
                        7,  // Dispersione orizzontale.
                        0,  // Dispersione verticale.
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.RED_FUNGUS.get())))
                ));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
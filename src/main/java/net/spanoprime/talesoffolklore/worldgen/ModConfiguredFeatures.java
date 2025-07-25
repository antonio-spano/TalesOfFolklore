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
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModFernBlock;
import net.spanoprime.talesoffolklore.worldgen.decorators.*;
import net.spanoprime.talesoffolklore.worldgen.feature.ModFeatures;

import java.util.ArrayList;
import java.util.List;

public class ModConfiguredFeatures {

    // Chiave per l'albero
    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STREAM_CARVER_KEY = registerKey("stream_carver");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_FUNGUS_KEY = registerKey("red_fungus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FERN_KEY = registerKey("fern");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FIREFLIES_BUSH_KEY = registerKey("fireflies_bush");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // --- VIRGINIA PINE ---
        List<TreeDecorator> decorators = new ArrayList<>();
        decorators.add(new ModWallMossDecorator(0.5f));
        decorators.add(new AlterGroundDecorator(
                new WeightedStateProvider(
                        SimpleWeightedRandomList.<BlockState>builder()
                                .add(ModBlocks.DAMP_PODZOL.get().defaultBlockState(), 1)
                                //.add(ModBlocks.LUSH_DIRT.get().defaultBlockState(), 2)
                                .add(ModBlocks.DAMP_COARSE_DIRT.get().defaultBlockState(), 1)
                )
        ));
        decorators.add(new ModWallIvyDecorator(0.2f));
        decorators.add(new ModPineNeedlesDecorator(.3f));
        decorators.add(new ModYellowFungusDecorator(.05f));
        decorators.add(new ModUndergrowthDecorator(.24f));

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

        register(context, RED_FUNGUS_KEY, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        BlockTags.DIRT, // Cerca solo blocchi di terra
                        BlockStateProvider.simple(ModBlocks.DAMP_GRASS_BLOCK.get().defaultBlockState()), // La "pezza" è di terra (rimpiazza terra con terra)
                        PlacementUtils.inlinePlaced(
                                Feature.SIMPLE_BLOCK, // La feature da piazzare SOPRA la pezza. Sì, è ancora SIMPLE_BLOCK, ma ora è nel posto giusto.
                                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.RED_FUNGUS.get()))
                        ),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),      // Profondità della pezza
                        0.8f,                   // extra_bottom_block_chance
                        3,                      // vertical_range
                        0.03f,                   // vegetation_chance: alta probabilità di piazzare il fungo sulla pezza
                        UniformInt.of(2, 5),    // raggio xz della pezza (molto piccolo)
                        0.3f                    // extra_edge_column_chance
                ));

        register(context, FERN_KEY, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        BlockTags.DIRT,
                        BlockStateProvider.simple(ModBlocks.DAMP_GRASS_BLOCK.get().defaultBlockState()),
                        PlacementUtils.inlinePlaced(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        // --- ECCO LA SOLUZIONE ---
                                        new RandomizedIntStateProvider(
                                                BlockStateProvider.simple(ModBlocks.FERN.get()), // Il blocco di base
                                                ModFernBlock.VARIANT,                           // La proprietà da randomizzare
                                                UniformInt.of(0, 1)                             // Il range di interi (inclusivo) da cui pescare
                                        )
                                )
                        ),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),
                        0.8f,
                        3,
                        0.2f,
                        UniformInt.of(3, 6),
                        0.3f
                ));

        register(context, FIREFLIES_BUSH_KEY, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        BlockTags.DIRT, // Cerca solo blocchi di terra
                        BlockStateProvider.simple(ModBlocks.DAMP_GRASS_BLOCK.get().defaultBlockState()), // La "pezza" è di terra (rimpiazza terra con terra)
                        PlacementUtils.inlinePlaced(
                                Feature.SIMPLE_BLOCK, // La feature da piazzare SOPRA la pezza. Sì, è ancora SIMPLE_BLOCK, ma ora è nel posto giusto.
                                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.FIREFLIES_BUSH.get()))
                        ),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),      // Profondità della pezza
                        0.8f,                   // extra_bottom_block_chance
                        3,                      // vertical_range
                        0.03f,                   // vegetation_chance: alta probabilità di piazzare il fungo sulla pezza
                        UniformInt.of(2, 5),    // raggio xz della pezza (molto piccolo)
                        0.3f                    // extra_edge_column_chance
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
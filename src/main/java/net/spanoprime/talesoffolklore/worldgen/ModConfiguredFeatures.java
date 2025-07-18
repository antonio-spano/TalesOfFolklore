package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
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
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallIvyDecorator;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallMossDecorator;

import java.util.ArrayList;
import java.util.List;

public class ModConfiguredFeatures {

    // Chiave per l'albero
    public static final ResourceKey<ConfiguredFeature<?, ?>> VIRGINIA_PINE_KEY = registerKey("virginia_pine");

    // Chiavi per le rocce della riva
    public static final ResourceKey<ConfiguredFeature<?, ?>> RIVERBANK_STONE_KEY = registerKey("riverbank_stone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RIVERBANK_COBBLESTONE_KEY = registerKey("riverbank_cobblestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RIVERBANK_MOSSY_COBBLESTONE_KEY = registerKey("riverbank_mossy_cobblestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RIVERBANK_ANDESITE_KEY = registerKey("riverbank_andesite");

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

        // --- RIVERBANK ROCKS ---
        RuleTest replaceableGround = new TagMatchTest(BlockTags.DIRT);

        // La dimensione della "vena" è 1 per piazzare blocchi singoli e creare una linea
        OreConfiguration stoneConfig = new OreConfiguration(List.of(OreConfiguration.target(replaceableGround, Blocks.STONE.defaultBlockState())), 1);
        OreConfiguration cobblestoneConfig = new OreConfiguration(List.of(OreConfiguration.target(replaceableGround, Blocks.COBBLESTONE.defaultBlockState())), 1);
        OreConfiguration mossyCobblestoneConfig = new OreConfiguration(List.of(OreConfiguration.target(replaceableGround, Blocks.MOSSY_COBBLESTONE.defaultBlockState())), 1);
        OreConfiguration andesiteConfig = new OreConfiguration(List.of(OreConfiguration.target(replaceableGround, Blocks.ANDESITE.defaultBlockState())), 1);

        register(context, RIVERBANK_STONE_KEY, Feature.ORE, stoneConfig);
        register(context, RIVERBANK_COBBLESTONE_KEY, Feature.ORE, cobblestoneConfig);
        register(context, RIVERBANK_MOSSY_COBBLESTONE_KEY, Feature.ORE, mossyCobblestoneConfig);
        register(context, RIVERBANK_ANDESITE_KEY, Feature.ORE, andesiteConfig);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
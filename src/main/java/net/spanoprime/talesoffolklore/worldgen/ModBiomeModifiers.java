package net.spanoprime.talesoffolklore.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_TREE_VIRGINIA_PINE = registerKey("add_tree_virginia_pine");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        /*
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_VIRGINIA_PINE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ResourceKey.create(
                        Registries.BIOME, // The biome registry
                        ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest") // Your custom biome key
                ))),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.VIRGINIA_PINE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION)); */
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, name));
    }
}
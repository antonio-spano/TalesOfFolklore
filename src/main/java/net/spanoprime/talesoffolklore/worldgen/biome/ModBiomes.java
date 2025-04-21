package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModBiomes
{
    public static final ResourceKey<Biome> APPALACHIAN_FOREST = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest"));

    public static void bootstrap(BootstapContext<Biome> context) {
        // Register the Appalachian Forest biome with checks for validity
        context.register(APPALACHIAN_FOREST, appalachianForest(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        // Add global overworld generation features
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

    public static Biome appalachianForest(BootstapContext<Biome> context) {
        // Build the spawning settings
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        // Obtain registry lookups safely
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        // Ensure both lookups are valid (fallback or throw an exception if needed)
        if (placedFeatures == null || configuredCarvers == null) {
            throw new IllegalStateException("Missing required registries for Appalachation Forest generation.");
        }

        // Build the biome generation settings
        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatures, configuredCarvers);
        globalOverworldGeneration(biomeBuilder);

        // Add specific features to the biome
        //BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        //BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
        BiomeDefaultFeatures.addFerns(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addExtraGold(biomeBuilder);
        BiomeDefaultFeatures.addPlainGrass(biomeBuilder); //grass

        // Safely add vanilla vegetation placements
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);

        // TODO: Handle ModPlacedFeatures safely if needed (remove invalid calls to getHolder())
        // biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.VIRGINIA_PINE_PLACED_KEY);

        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);

        // Build and return the actual Biome object with custom visuals and overrides
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.5f) // default downfall
                .temperature(0.5f) // default temperature
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .fogColor(0xEAEAEA) // grigio chiaro
                        .waterColor(0x3F76E4) // default water color
                        .waterFogColor(0x050533) // default water fog color
                        .skyColor(0x78A7FF) // default sky color (calculated based on temperature)
                        .grassColorOverride(0xA7C191) // tundra grass color
                        //.foliageColorOverride(null) // default foliage color (remove override)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS) // optional, kept from original
                        .build())
                .build();
    }
}
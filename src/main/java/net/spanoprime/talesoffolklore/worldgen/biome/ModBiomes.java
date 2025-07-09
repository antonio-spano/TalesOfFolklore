package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.ModPlacedFeatures;

public class ModBiomes
{
    public static final ResourceKey<Biome> APPALACHIAN_FOREST = ResourceKey.create(Registries.BIOME,
            new ResourceLocation(TalesOfFolklore.MOD_ID, "appalachian_forest"));

    public static void bootstrap(BootstapContext<Biome> context) {
        context.register(APPALACHIAN_FOREST, appalachianForest(context));
    }

    public static Biome appalachianForest(BootstapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatures, configuredCarvers);

        // --- INIZIO MODIFICA FONDAMENTALE ---
        // Aggiungiamo MANUALMENTE solo quello che vogliamo.
        // NIENTE carvers, NIENTE laghi, NIENTE sorgenti.

        //BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
/*
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_DIRT));
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_GRAVEL));
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_GRANITE));
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_DIORITE));
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_ANDESITE));
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(VanillaPlacedFeatures.ORE_TUFF));
*/
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addSurfaceFreezing(biomeBuilder);

        BiomeDefaultFeatures.addCherryGroveVegetation(biomeBuilder);

        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_dirt")
                ))
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_gravel")
                ))
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_granite")
                ))
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_diorite")
                ))
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_andesite")
                ))
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                placedFeatures.getOrThrow(ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        new ResourceLocation("minecraft", "ore_tuff")
                ))
        );
        //BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        //BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder); // ← Aggiunge erba alta!
        //BiomeDefaultFeatures.addFerns(biomeBuilder);                  // ← Aggiunge anche felci
        //BiomeDefaultFeatures.addForestGrass(biomeBuilder);            // ← OK
        //BiomeDefaultFeatures.addDefaultGrass(biomeBuilder);

        // Aggiungiamo i tuoi alberi custom
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.VIRGINIA_PINE_PLACED_KEY);
        // --- FINE MODIFICA FONDAMENTALE ---


        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.6f)
                .temperature(0.3f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .fogColor(0xDADADA)
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .skyColor(calculateSkyColor(0.3f))
                        .grassColorOverride(0x80A755)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build())
                .build();
    }

    private static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }
}
package net.spanoprime.talesoffolklore.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements; // Assicurati sia importato
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.ModPlacedFeatures;
import org.joml.Math;
// Assumi che questo esista se lo userai per i tuoi alberi
// import net.spanoprime.talesoffolklore.worldgen.ModPlacedFeatures;

public class ModBiomes
{
    public static final ResourceKey<Biome> APPALACHIAN_FOREST = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest"));

    public static void bootstrap(BootstapContext<Biome> context) {
        context.register(APPALACHIAN_FOREST, appalachianForest(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

    public static Biome appalachianForest(BootstapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder); // Include pipistrelli, ragni, zombie, etc.

        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        // Rimosso controllo null perché BootstrapContext dovrebbe garantirli
        // if (placedFeatures == null || configuredCarvers == null) {
        //     throw new IllegalStateException("Missing required registries for Appalachian Forest generation.");
        // }

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatures, configuredCarvers);
        globalOverworldGeneration(biomeBuilder);

        // --- Generazione Features Specifiche ---
        //BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder); // Rocce coperte di muschio? Considera se adatte
        BiomeDefaultFeatures.addFerns(biomeBuilder); // Felci
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder); // Minerali standard
        // BiomeDefaultFeatures.addExtraGold(biomeBuilder); // Oro extra? Potrebbe starci tematicamente
        BiomeDefaultFeatures.addPlainGrass(biomeBuilder); // Erba base

        // --- ALBERI ---
        // Usa alberi più fitti di Plains ma meno specifici di Taiga
        //biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS); // Querce e Betulle più fitte

        // TODO: Aggiungi qui i tuoi alberi custom quando pronti
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.VIRGINIA_PINE_PLACED_KEY);

        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder); // Funghi standard
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder); // Canna da zucchero, zucche

        // --- Biome Properties & Effects ---
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.6f)
                .temperature(0.3f) // Leggermente freddo/temperato
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .fogColor(0xDADADA) // Grigio chiaro per la nebbia
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .skyColor(calculateSkyColor(0.3f)) // Calcolato dinamicamente
                        .grassColorOverride(0x80A755) // Verde erba stile foresta temperata/leggermente fredda
                        //.foliageColorOverride(0x60884D) // Verde fogliame abbinato (Opzionale)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build())
                .build();
    }

    // Helper method per il colore del cielo basato sulla temperatura (vanilla lo fa così)
    private static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    // Bisogna importare Mth
    // import net.minecraft.util.Mth;
}
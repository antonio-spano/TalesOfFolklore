package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes; // NECESSARIO
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils; // Import verificato
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder; // Necessario

import java.util.function.Consumer;

public class ModOverworldRegion extends Region
{
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight); // Riceve 20
    }
/*
    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        //VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        this.addModifiedVanillaOverworldBiomes(mapper, modifiedVanillaOverworldBuilder -> {
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.BADLANDS, ModBiomes.APPALACHIAN_FOREST);
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.ERODED_BADLANDS, ModBiomes.APPALACHIAN_FOREST);
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.WOODED_BADLANDS, ModBiomes.APPALACHIAN_FOREST);
        });

        // ULTIMO TENTATIVO PARAMETRI: MUSHROOM + FULL_RANGE + PESO ESTREMO
        // Se questo non sostituisce completamente, Terrablender ha limiti qui.
/*
        new ParameterUtils.ParameterPointListBuilder()
                .continentalness(ParameterUtils.Continentalness.MUSHROOM_FIELDS)
                .temperature(ParameterUtils.Temperature.FULL_RANGE)
                .humidity(ParameterUtils.Humidity.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.APPALACHIAN_FOREST));

        builder.build().forEach(mapper::accept);

        // NOTA: Se questo fallisce, il metodo replaceBiome() è teoricamente più pulito,
        // ma se nemmeno quello ha funzionato, il problema è più profondo.
    } */

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        var points = new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.COOL)
                .humidity(ParameterUtils.Humidity.WET)
                .continentalness(Climate.Parameter.span(0.75f, 1.0f)) // zona molto interna, lontano dallo spawn
                .erosion(Climate.Parameter.span(-0.375f, -0.25f))
                .depth(Climate.Parameter.point(0.0f))
                .weirdness(Climate.Parameter.span(0.6f, 0.9f))
                .build();

        for (var point : points) {
            mapper.accept(Pair.of(point, ModBiomes.APPALACHIAN_FOREST));
        }
    }
}
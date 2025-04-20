package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class ModOverworldRegion extends Region
{
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        /* "EASY" WAY
        this.addModifiedVanillaOverworldBiomes(mapper, modifiedVanillaOverworldBuilder -> {
            modifiedVanillaOverworldBuilder.replaceBiome(Biomes.FOREST, ModBiomes.APPALACHIAN_FOREST);
        }); */

        //"PROPER" WAY
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
// Overlap Vanilla's parameters with our own for our COLD_BLUE biome.
// The parameters for this biome are chosen arbitrarily.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature. span(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature. FROZEN))
                .humidity(ParameterUtils.Humidity. span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY))
                .continentalness(ParameterUtils.Continentalness.INLAND)
                .erosion (ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)
                .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth. FLOOR)
                .weirdness(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING)
                .build(). forEach(parameterPoint ->  builder.add(parameterPoint, ModBiomes.APPALACHIAN_FOREST));
        //TODO: OCCHIO AI PARAMETERPOINTS DOVREBBERO ESSERE POINT
// Add our points to the mapper
        builder.build().forEach(mapper:: accept);
    }
}

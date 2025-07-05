package net.spanoprime.talesoffolklore.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class ModOverworldRegion extends Region {
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // L'obiettivo è definire una "fetta climatica" per un bioma montuoso, grande e raro.
        // Usiamo le costanti di ParameterUtils per una maggiore leggibilità e compatibilità.

        var points = new ParameterUtils.ParameterPointListBuilder()
                // TEMPERATURA E UMIDITÀ: Un range temperato/freddo e umido, tipico degli Appalachi.
                .temperature(ParameterUtils.Temperature.span(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL))
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))

                // CONTINENTALNESS: La chiave per essere "lontano dallo spawn".
                // Vogliamo che il bioma si generi nelle zone interne del continente, non sulle coste.
                .continentalness(ParameterUtils.Continentalness.FAR_INLAND)

                // EROSION: La chiave per un terreno "interessante".
                // Un range ampio crea valli, pendii e cime, evitando un paesaggio monotono.
                // EROSION_0 = molto eroso (piatto), EROSION_6 = poco eroso (frastagliato).
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_5))

                // DEPTH: La chiave per essere "montuoso".
                // Questo è il parametro più importante. Vogliamo che si generi da colline (surface) a cime (peaks).
                // Questo crea l'elevazione desiderata.
                .depth(Climate.Parameter.span(0.3f, 1.2f))

                // WEIRDNESS: La chiave per la "rarità".
                // Il mondo è diviso in "slice" di weirdness. Scegliendone una non centrale, rendiamo il bioma raro.
                // Usiamo un range che si trovi nella parte "alta" della variazione.
                .weirdness(ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_DESCENDING))

                .build();

        // Applica il nostro bioma a tutti i punti climatici che rientrano in questa definizione.
        points.forEach(point -> mapper.accept(Pair.of(point, ModBiomes.APPALACHIAN_FOREST)));
    }
}
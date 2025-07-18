package net.spanoprime.talesoffolklore.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.biome.surface.ModSurfaceRules;

public class ModNoiseGeneratorSettingsProvider {

    public static final ResourceKey<NoiseGeneratorSettings> APPALACHIAN_SETTINGS =
            ResourceKey.create(Registries.NOISE_SETTINGS, new ResourceLocation(TalesOfFolklore.MOD_ID, "appalachian_noise_settings"));

    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
        // 1. Creiamo un'istanza delle impostazioni vanilla dell'Overworld.
        //    Questo ci dà un oggetto di base con tutti i valori corretti.
        NoiseGeneratorSettings vanillaOverworld = NoiseGeneratorSettings.overworld(context, false, false);

        // 2. Prendiamo le regole di superficie vanilla da quell'oggetto.
        SurfaceRules.RuleSource vanillaSurfaceRule = vanillaOverworld.surfaceRule();

        // 3. Creiamo la nostra regola custom per il fiume.
        SurfaceRules.RuleSource riverRule = ModSurfaceRules.makeRiverOnlyRule();

        // 4. Uniamo le due regole: la nostra avrà la priorità.
        //    Se non siamo nel bioma del fiume, verranno usate le regole vanilla.
        SurfaceRules.RuleSource finalSurfaceRules = SurfaceRules.sequence(riverRule, vanillaSurfaceRule);

        // 5. Creiamo il nostro NoiseGeneratorSettings custom usando il costruttore a 11 parametri.
        //    Copiamo ogni parametro dall'oggetto vanilla, tranne la surfaceRule.
        NoiseGeneratorSettings customSettings = new NoiseGeneratorSettings(
                vanillaOverworld.noiseSettings(),
                vanillaOverworld.defaultBlock(),
                vanillaOverworld.defaultFluid(),
                vanillaOverworld.noiseRouter(),
                finalSurfaceRules, // <-- L'UNICA COSA CHE CAMBIAMO
                vanillaOverworld.spawnTarget(),
                vanillaOverworld.seaLevel(),
                vanillaOverworld.disableMobGeneration(),
                vanillaOverworld.isAquifersEnabled(), // Usa il getter corretto
                vanillaOverworld.oreVeinsEnabled(),
                vanillaOverworld.useLegacyRandomSource()
        );

        // 6. Registriamo le nostre impostazioni finali.
        context.register(APPALACHIAN_SETTINGS, customSettings);
    }
}
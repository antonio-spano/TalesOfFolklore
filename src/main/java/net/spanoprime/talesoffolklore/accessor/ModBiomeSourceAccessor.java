package net.spanoprime.talesoffolklore.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface ModBiomeSourceAccessor {
    /**
     * Aggiunge un solo biomeHolder alla lista dei possibili,
     * se non è già stato fatto.
     */
    void letsVinoCryptids$modExpandBiome(Holder<Biome> biomeHolder);
}

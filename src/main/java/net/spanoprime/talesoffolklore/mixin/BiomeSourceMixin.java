package net.spanoprime.talesoffolklore.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.spanoprime.talesoffolklore.accessor.ModBiomeSourceAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements ModBiomeSourceAccessor {
    // Shadow del supplier privato finale in BiomeSource
    @Shadow
    private Supplier<Set<Holder<Biome>>> possibleBiomes;

    // Flag per non espandere più volte
    private boolean expanded = false;

    /**
     * Questo metodo viene invocato dal tuo ChunkStatusMixin,
     * e aggiunge il tuo BiomeHolder alla lista dei possibili biomi.
     */
    @Override
    public void letsVinoCryptids$modExpandBiome(Holder<Biome> biomeHolder) {
        if (!expanded) {
            // Ricostruisci il set: originali + nostro
            ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
            builder.addAll(possibleBiomes.get());
            builder.add(biomeHolder);

            // Sovrascrivi il supplier con la nuova istanza memoizzata
            this.possibleBiomes = Suppliers.memoize(builder::build);
            expanded = true;
        }
    }
}

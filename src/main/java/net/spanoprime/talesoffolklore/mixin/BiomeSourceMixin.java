package net.spanoprime.talesoffolklore.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.spanoprime.talesoffolklore.accessor.ModBiomeSourceAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements ModBiomeSourceAccessor {
    @Shadow @Mutable
    private Supplier<Set<Holder<Biome>>> possibleBiomes;

    @Unique
    private boolean talesoffolklore$isExpanded = false;

    @Override
    public void letsVinoCryptids$modExpandBiome(Holder<Biome> biomeHolder) {
        if (!this.talesoffolklore$isExpanded && biomeHolder != null) {
            ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
            builder.addAll(this.possibleBiomes.get());
            builder.add(biomeHolder);
            this.possibleBiomes = Suppliers.memoize(builder::build);
            this.talesoffolklore$isExpanded = true;
            System.out.println("[TALES OF FOLKLORE] BIOME REGISTRAR: Appalachian Forest is now officially in the BiomeSource list.");
        }
    }
}
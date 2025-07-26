package net.spanoprime.talesoffolklore.mixin;

import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.spanoprime.talesoffolklore.accessor.ModBiomeSourceAccessor;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import org.spongepowered.asm.mixin.*;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin implements ModBiomeSourceAccessor {
    // Shadow del supplier originale
    @Shadow private Supplier<Set<Holder<Biome>>> possibleBiomes;

    @Unique private boolean tof_biomeExpanded = false;

    @Override
    public void letsVinoCryptids$modExpandBiome(Holder<Biome> biomeHolder) {
        if (!tof_biomeExpanded) {
            // costruiamo un nuovo supplier che unisce quelli esistenti + il nostro
            ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
            builder.addAll(possibleBiomes.get());
            builder.add(biomeHolder);
            this.possibleBiomes = Suppliers.memoize(builder::build);
            tof_biomeExpanded = true;
        }
    }
}

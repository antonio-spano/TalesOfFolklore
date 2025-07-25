package net.spanoprime.talesoffolklore.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModWorldGen {
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCE_TYPES =
            DeferredRegister.create(Registries.BIOME_SOURCE, TalesOfFolklore.MOD_ID);

    /*public static final RegistryObject<Codec<WrapperBiomeSource>> WRAPPER_BIOME_SOURCE =
            BIOME_SOURCE_TYPES.register("wrapper_biome_source", () -> WrapperBiomeSource.CODEC);*/

    public static void register(IEventBus eventBus) {
        BIOME_SOURCE_TYPES.register(eventBus);
    }
}
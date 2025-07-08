package net.spanoprime.talesoffolklore.util; // o dove metti i tag

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModTags {
    public static class Structures {
        public static final TagKey<Structure> ON_APPALACHIAN_MAP =
                TagKey.create(Registries.STRUCTURE, new ResourceLocation(TalesOfFolklore.MOD_ID, "on_appalachian_map"));
        // Helper per evitare di creare tag vuoti, non strettamente necessario
        private static TagKey<Structure> tag(String name) {
            return TagKey.create(Registries.STRUCTURE, new ResourceLocation(TalesOfFolklore.MOD_ID, name));
        }
    }
}
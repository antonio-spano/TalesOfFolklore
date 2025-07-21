package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {

    @Shadow private List<WorldCreationUiState.WorldTypeEntry> normalPresetList;
    @Shadow private List<WorldCreationUiState.WorldTypeEntry> altPresetList;

    // Chiamiamo il setter, non tocchiamo direttamente il campo worldType
    @Shadow public abstract void setWorldType(WorldCreationUiState.WorldTypeEntry entry);

    private static final ResourceLocation MY_PRESET_ID =
            new ResourceLocation("talesoffolklore", "talesoffolklore");

    @Inject(method = "updatePresetLists", at = @At("TAIL"))
    private void tof$moveAndSelectMyPreset(CallbackInfo ci) {
        // 1) muovi il preset in testa ad entrambe le liste
        moveFirst(normalPresetList);
        moveFirst(altPresetList);

        // 2) e selezionalo davvero, usando il setter
        WorldCreationUiState.WorldTypeEntry first = normalPresetList.get(0);
        if (isMyPreset(first)) {
            setWorldType(first);
        }
    }

    private static void moveFirst(List<WorldCreationUiState.WorldTypeEntry> list) {
        for (int i = 0; i < list.size(); i++) {
            if (isMyPreset(list.get(i))) {
                if (i != 0) {
                    Collections.swap(list, 0, i);
                }
                break;
            }
        }
    }

    private static boolean isMyPreset(WorldCreationUiState.WorldTypeEntry entry) {
        Holder<WorldPreset> h = entry.preset();
        return h != null &&
                h.unwrapKey()
                        .map(k -> k.location())
                        .filter(MY_PRESET_ID::equals)
                        .isPresent();
    }
}

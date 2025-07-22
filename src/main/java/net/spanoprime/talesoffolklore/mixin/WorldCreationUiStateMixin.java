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

    // Shadow del tuo setter
    @Shadow public abstract void setWorldType(WorldCreationUiState.WorldTypeEntry entry);

    private static final ResourceLocation MY_PRESET_ID =
            new ResourceLocation("talesoffolklore", "talesoffolklore");

    // Injectiamo al RETURN di updatePresetLists() con la signature completa
    @Inject(
            method = "updatePresetLists()V",
            at = @At("RETURN")
    )
    private void tof$moveAndSelectMyPreset(CallbackInfo ci) {
        // 1) Sposta il nostro preset in testa
        moveFirst(normalPresetList);
        moveFirst(altPresetList);

        // 2) Se è il primo, selezionalo via setter
        if (!normalPresetList.isEmpty()) {
            WorldCreationUiState.WorldTypeEntry candidate = normalPresetList.get(0);
            if (isMyPreset(candidate)) {
                setWorldType(candidate);
            }
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

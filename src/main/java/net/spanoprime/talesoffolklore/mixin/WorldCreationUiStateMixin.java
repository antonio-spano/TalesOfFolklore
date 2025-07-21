package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {

    /** liste che la classe riempie in updatePresetLists() */
    @Shadow private List<WorldCreationUiState.WorldTypeEntry> normalPresetList;
    @Shadow private List<WorldCreationUiState.WorldTypeEntry> altPresetList;

    /** preset attualmente selezionato */
    @Shadow @Mutable private WorldCreationUiState.WorldTypeEntry worldType;

    private static final ResourceLocation MY_PRESET_ID =
            new ResourceLocation("talesoffolklore", "talesoffolklore");

    /** sposta il preset in testa **dopo** che la lista è stata popolata */
    @Inject(method = "updatePresetLists", at = @At("TAIL"))
    private void tof$moveMyPresetFirst(CallbackInfo ci) {
        // 1) mettiamo il preset in testa in entrambe le liste
        moveFirst(normalPresetList);
        moveFirst(altPresetList);

        // 2) se non è già selezionato, lo selezioniamo noi
        if (!isMyPreset(worldType)) {
            worldType = normalPresetList.get(0);
        }
    }

    /* ---------- utility ---------- */

    private static void moveFirst(List<WorldCreationUiState.WorldTypeEntry> list) {
        for (int i = 0; i < list.size(); i++) {
            if (isMyPreset(list.get(i))) {
                if (i != 0) {                      // evita swap inutile
                    Collections.swap(list, 0, i);
                }
                break;
            }
        }
    }

    private static boolean isMyPreset(WorldCreationUiState.WorldTypeEntry entry) {
        Holder<WorldPreset> h = entry.preset();
        return h != null &&
                h.unwrapKey().map(k -> k.location())
                        .filter(MY_PRESET_ID::equals)
                        .isPresent();
    }
}

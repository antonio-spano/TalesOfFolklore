package net.spanoprime.talesoffolklore.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Salta la ConfirmScreen “experimental settings” aprendo subito il mondo.
 * Usa @Inject invece di @Redirect per massima compatibilità con OptiFine.
 */
@Mixin(WorldOpenFlows.class)
public abstract class NoExperimentalWarningMixin {

    /**
     * Invece di usare un @Redirect fragile, usiamo @Inject all'inizio del metodo.
     * Eseguiamo subito il callback che crea il mondo e poi cancelliamo l'esecuzione
     * del metodo originale, impedendo alla schermata di avviso di apparire.
     * require = 0 è una sicurezza: se un altro mod (o un futuro OptiFine)
     * rimuove completamente questo metodo, il gioco non crasherà. Semplicemente,
     * il nostro mixin non verrà applicato.
     */
    @Inject(
            method = "confirmWorldCreation",
            at = @At("HEAD"),
            cancellable = true,
            require = 0 // A prova di proiettile: se non trova il metodo, non crashare.
    )
    private static void tof$skipExperimentalConfirm(
            Minecraft minecraft,
            CreateWorldScreen createWorldScreen,
            Lifecycle lifecycle,
            Runnable runnable,
            boolean bl,
            CallbackInfo ci) { // CallbackInfo ci serve per cancellare il metodo originale.

        // Eseguiamo l'azione che crea il mondo
        runnable.run();

        // Cancelliamo il resto del metodo vanilla.
        // La chiamata a Minecraft.setScreen(new ConfirmScreen(...)) non verrà mai eseguita.
        ci.cancel();
    }
}
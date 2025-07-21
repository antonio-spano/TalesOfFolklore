package net.spanoprime.talesoffolklore.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Salta la ConfirmScreen “experimental settings” aprendo subito il mondo.
 */
@Mixin(WorldOpenFlows.class)
public abstract class NoExperimentalWarningMixin {

    /**
     * In WorldOpenFlows.confirmWorldCreation (…) Mojang chiama
     *   Minecraft#setScreen(new ConfirmScreen(…))
     * per mostrare l’avviso.  Con un @Redirect intercettiamo
     * *quella* singola invocazione e, invece di aprire la GUI,
     * eseguiamo direttamente il callback che genera il mondo.
     */
    @Redirect(
            method = "confirmWorldCreation",               // target static method
            at = @At( value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V" )
    )
    private static void tof$skipExperimentalConfirm(
            Minecraft mc,                       // receiver di setScreen
            Screen screen,                      // ConfirmScreen che NON vogliamo
            Minecraft self,                     // ↓ parametri originali di confirmWorldCreation
            CreateWorldScreen createScreen,
            Lifecycle lifecycle,
            Runnable createCallback,
            boolean showPreview) {

        // Saltiamo la conferma: creiamo subito il mondo
        createCallback.run();
        // Se vuoi tornare al menu dopo la creazione, togli questa riga
        // mc.setScreen(null);
    }
}

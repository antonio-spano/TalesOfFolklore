package net.spanoprime.talesoffolklore.handlers;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID, value = Dist.CLIENT)
public class FogHandler {

    private static final float MIN_FOG = 30.0f;
    private static final float MAX_FOG = 300.0f;
    private static final float FOG_SPEED = 5.0f;

    private static float currentFogDistance = MAX_FOG;
    private static boolean shouldRenderFog = false;
    private static boolean isInBiome = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var biomeKey = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        boolean inTargetBiome = biomeKey.isPresent() &&
                biomeKey.get().location().equals(ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest"));

        // Entrata nel bioma → attiva la nebbia
        if (inTargetBiome && !isInBiome) {
            isInBiome = true;
            shouldRenderFog = true;
        }

        // Uscita dal bioma → inizia a spegnere la nebbia
        if (!inTargetBiome && isInBiome) {
            isInBiome = false;
        }

        // Gestione transizione
        if (isInBiome) {
            currentFogDistance = Math.max(MIN_FOG, currentFogDistance - FOG_SPEED);
        } else {
            if (shouldRenderFog) {
                currentFogDistance = Math.min(MAX_FOG, currentFogDistance + FOG_SPEED);
                if (currentFogDistance >= MAX_FOG) {
                    shouldRenderFog = false; // disattiva nebbia quando è completamente "svanita"
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        if (!shouldRenderFog) return;

        float near = 0.0f;
        float far = currentFogDistance;

        event.setCanceled(true);
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        event.setFogShape(FogShape.SPHERE);

        RenderSystem.setShaderFogStart(near);
        RenderSystem.setShaderFogEnd(far);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!shouldRenderFog) return;

        // Grigio pallido per l’atmosfera misteriosa
        float r = 216f / 255f;
        float g = 216f / 255f;
        float b = 216f / 255f;

        // Leggero blending col colore esistente (se vuoi intensità dinamica, fammi sapere)
        float blend = 0.7f;
        float finalR = event.getRed() * (1 - blend) + r * blend;
        float finalG = event.getGreen() * (1 - blend) + g * blend;
        float finalB = event.getBlue() * (1 - blend) + b * blend;

        event.setRed(finalR);
        event.setGreen(finalG);
        event.setBlue(finalB);
    }
}
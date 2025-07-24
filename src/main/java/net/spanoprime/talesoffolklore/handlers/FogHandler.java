package net.spanoprime.talesoffolklore.handlers;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth; // Import per Mth.lerp
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes; // Usa la chiave del bioma direttamente
import org.lwjgl.system.MathUtil;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID, value = Dist.CLIENT)
public class FogHandler {

    private static final float MIN_FOG_DISTANCE = 250.0f; // Distanza nebbia minima (più vicina)
    private static final float MAX_FOG_DISTANCE = 2000.0f; // Distanza nebbia massima (default, lontana)
    private static final float FOG_TRANSITION_SPEED = 0.05f; // Velocità di transizione (più basso = più lento/smooth)

    private static float currentFogDistance = MAX_FOG_DISTANCE;
    private static float targetFogDistance = MAX_FOG_DISTANCE;
    private static boolean renderCustomFog = false; // Flag per attivare il rendering custom

    // Helper per interpolazione lineare
    private static float lerp(float start, float end, float delta) {
        return Mth.lerp(delta, start, end); // Usa Mth.lerp per correttezza
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Controlla se il giocatore è nel bioma target
        boolean isInTargetBiome = mc.level.getBiome(mc.player.blockPosition()).is(ModBiomes.APPALACHIAN_FOREST);

        // Imposta la distanza target della nebbia
        if (isInTargetBiome) {
            targetFogDistance = MIN_FOG_DISTANCE;
        } else {
            targetFogDistance = MAX_FOG_DISTANCE;
        }

        // Interpola dolcemente la distanza corrente verso la distanza target
        currentFogDistance = lerp(currentFogDistance, targetFogDistance, FOG_TRANSITION_SPEED);

        // Determina se dobbiamo applicare la nebbia custom
        // Attiva se siamo nel bioma o se la nebbia non è ancora completamente dissolta
        renderCustomFog = isInTargetBiome || !Mth.equal(currentFogDistance, MAX_FOG_DISTANCE);

        // Piccola soglia per evitare calcoli/rendering quando la nebbia è praticamente sparita
        if (Math.abs(currentFogDistance - MAX_FOG_DISTANCE) < 0.1f) {
            currentFogDistance = MAX_FOG_DISTANCE; // Snap alla distanza massima
            if (!isInTargetBiome) renderCustomFog = false; // Disattiva se siamo fuori e la nebbia è a max
        }
        if (Math.abs(currentFogDistance - MIN_FOG_DISTANCE) < 0.1f) {
            currentFogDistance = MIN_FOG_DISTANCE; // Snap alla distanza minima
        }
    }

    @SubscribeEvent
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        // Applica solo se il flag è attivo e non siamo in modalità spettatore o acqua
        if (!renderCustomFog || event.getCamera().getEntity().isSpectator() || event.getCamera().getFluidInCamera().equals(net.minecraft.tags.FluidTags.WATER)) {
            return;
        }

        float near = 0.0f; // Puoi regolare il near plane se necessario, ma 0 di solito va bene
        float far = currentFogDistance;

        event.setCanceled(true);
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        event.setFogShape(FogShape.SPHERE); // Nebbia sferica

        // Imposta i valori per lo shader della nebbia
        RenderSystem.setShaderFogStart(near);
        RenderSystem.setShaderFogEnd(far);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!renderCustomFog
                || event.getCamera().getEntity().isSpectator()
                || event.getCamera().getFluidInCamera().equals(net.minecraft.tags.FluidTags.WATER)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        // Prendiamo il colore del cielo al tick corrente
        Vec3 skyColor = mc.level.getSkyColor(event.getCamera().getPosition(), mc.getFrameTime());

        float targetR = (float) skyColor.x;
        float targetG = (float) skyColor.y;
        float targetB = (float) skyColor.z;

        // Calcoliamo il blend factor in base alla densità della nebbia
        float blendFactor = Mth.clamp(
                1.0f - (currentFogDistance - MIN_FOG_DISTANCE) / (MAX_FOG_DISTANCE - MIN_FOG_DISTANCE),
                0.0f, 1.0f
        );

        // Interpoliamo dolcemente dal colore “vanilla” (event.getX()) al colore del cielo
        float finalR = lerp(event.getRed(),   targetR, blendFactor);
        float finalG = lerp(event.getGreen(), targetG, blendFactor);
        float finalB = lerp(event.getBlue(),  targetB, blendFactor);

        event.setRed(finalR);
        event.setGreen(finalG);
        event.setBlue(finalB);
    }

}
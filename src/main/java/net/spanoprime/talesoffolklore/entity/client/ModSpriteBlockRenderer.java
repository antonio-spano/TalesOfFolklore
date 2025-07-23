package net.spanoprime.talesoffolklore.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.entity.ModSpriteBlockEntity;

public class ModSpriteBlockRenderer implements BlockEntityRenderer<ModSpriteBlockEntity> {

    // La texture del tuo sprite. Mettila in assets/talesoffolklore/textures/block/fire_sprite.png
    private static final ResourceLocation SPRITE_TEXTURE = new ResourceLocation(TalesOfFolklore.MOD_ID, "block/fire_sprite");

    public ModSpriteBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ModSpriteBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        // --- LOGICA DI DISSOLVENZA ---
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 blockPos = Vec3.atCenterOf(pBlockEntity.getBlockPos());
        double distance = cameraPos.distanceTo(blockPos);

        float minDistance = 10f;
        float maxDistance = 50f;

        // Calcoliamo l'alpha (trasparenza) in base alla distanza.
        // Mth.inverseLerp calcola a che punto % si trova 'distance' tra 'min' e 'max'.
        // Il risultato viene "clampato" tra 0.0 e 1.0 per sicurezza.
        float alpha = (float) Mth.clamp(Mth.inverseLerp(distance, minDistance, maxDistance), 0.0, 1.0);

        // Se è completamente invisibile, non disegniamo nulla.
        if (alpha <= 0.0f) {
            return;
        }

        // --- LOGICA BILLBOARD E DISEGNO ---
        pPoseStack.pushPose();

        // Spostiamoci al centro del blocco
        pPoseStack.translate(0.5, 0.5, 0.5);

        // Applichiamo la rotazione della telecamera per far sì che lo sprite ci guardi sempre.
        pPoseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        // Potrebbe servire una rotazione aggiuntiva se lo sprite è "sdraiato"
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180f));

        // Prendiamo lo sprite dalla texture atlas del gioco
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SPRITE_TEXTURE);
        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.translucent());

        // Disegniamo il quad (il pannello 2D)
        addVertex(vertexConsumer, pPoseStack, -0.5f, -0.5f, 0, sprite.getU0(), sprite.getV1(), pPackedLight, alpha);
        addVertex(vertexConsumer, pPoseStack, -0.5f, 0.5f, 0, sprite.getU0(), sprite.getV0(), pPackedLight, alpha);
        addVertex(vertexConsumer, pPoseStack, 0.5f, 0.5f, 0, sprite.getU1(), sprite.getV0(), pPackedLight, alpha);
        addVertex(vertexConsumer, pPoseStack, 0.5f, -0.5f, 0, sprite.getU1(), sprite.getV1(), pPackedLight, alpha);

        pPoseStack.popPose();
    }

    // Metodo helper per aggiungere un vertice al nostro quad.
    private static void addVertex(VertexConsumer consumer, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, float alpha) {
        consumer.vertex(poseStack.last().pose(), x, y, z)
                .color(1.0f, 1.0f, 1.0f, alpha) // Applichiamo l'alpha calcolato qui!
                .uv(u, v)
                .uv2(packedLight)
                .normal(1, 0, 0)
                .endVertex();
    }
}
package net.spanoprime.talesoffolklore.entity.client;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.entity.ModBoatEntity;
import net.spanoprime.talesoffolklore.entity.ModChestBoatEntity;

import java.util.EnumMap;
import java.util.Map;

public class ModBoatRenderer extends BoatRenderer {

    private final Map<ModBoatEntity.Type, ResourceModelPair> boatResources;

    public ModBoatRenderer(EntityRendererProvider.Context pContext, boolean pChestBoat) {
        super(pContext, pChestBoat);

        // Uso EnumMap per efficienza con enum
        this.boatResources = new EnumMap<>(ModBoatEntity.Type.class);

        for (ModBoatEntity.Type type : ModBoatEntity.Type.values()) {
            ResourceLocation texture = new ResourceLocation(TalesOfFolklore.MOD_ID, getTextureLocation(type, pChestBoat));
            ListModel<Boat> model = createBoatModel(pContext, type, pChestBoat);
            boatResources.put(type, new ResourceModelPair(texture, model));
        }
    }

    private static String getTextureLocation(ModBoatEntity.Type pType, boolean pChestBoat) {
        return pChestBoat
                ? "textures/entity/chest_boat/" + pType.getName() + ".png"
                : "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, ModBoatEntity.Type pType, boolean pChestBoat) {
        ModelLayerLocation modelLayerLocation = pChestBoat
                ? ModBoatRenderer.createChestBoatModelName(pType)
                : ModBoatRenderer.createBoatModelName(pType);

        ModelPart modelPart = pContext.bakeLayer(modelLayerLocation);
        return pChestBoat ? new ChestBoatModel(modelPart) : new BoatModel(modelPart);
    }

    public static ModelLayerLocation createBoatModelName(ModBoatEntity.Type pType) {
        return createLocation("boat/" + pType.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(ModBoatEntity.Type pType) {
        return createLocation("chest_boat/" + pType.getName(), "main");
    }

    private static ModelLayerLocation createLocation(String path, String model) {
        return new ModelLayerLocation(new ResourceLocation(TalesOfFolklore.MOD_ID, path), model);
    }

    // Niente @Override qui!
    public ResourceModelPair getModModelWithLocation(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            return this.boatResources.get(modBoat.getModVariant());
        } else if (boat instanceof ModChestBoatEntity modChestBoatEntity) {
            return this.boatResources.get(modChestBoatEntity.getModVariant());
        } else {
            return null;
        }
    }


    // Semplice sostituto di Pair<ResourceLocation, ListModel<Boat>>
    public static class ResourceModelPair {
        private final ResourceLocation texture;
        private final ListModel<Boat> model;

        public ResourceModelPair(ResourceLocation texture, ListModel<Boat> model) {
            this.texture = texture;
            this.model = model;
        }

        public ResourceLocation texture() {
            return texture;
        }

        public ListModel<Boat> model() {
            return model;
        }
    }
}

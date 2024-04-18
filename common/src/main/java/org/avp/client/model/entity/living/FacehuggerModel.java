package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.entity.living.Facehugger;

public class FacehuggerModel extends GeoModel<Facehugger> {

    private static final String ENTITY_NAME = "facehugger";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(Facehugger entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Facehugger entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(Facehugger entity) {
        return ANIMATION_LOCATION;
    }
}

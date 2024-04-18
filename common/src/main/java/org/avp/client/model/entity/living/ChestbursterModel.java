package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.entity.living.Chestburster;

public class ChestbursterModel extends GeoModel<Chestburster> {

    private static final String ENTITY_NAME = "chestburster";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(Chestburster entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Chestburster entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(Chestburster entity) {
        return ANIMATION_LOCATION;
    }
}

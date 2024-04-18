package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.entity.living.OvamorphDraco;

public class OvamorphDracoModel extends GeoModel<OvamorphDraco> {

    private static final String ENTITY_NAME = "ovamorph_draco";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(OvamorphDraco entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(OvamorphDraco entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(OvamorphDraco entity) {
        return ANIMATION_LOCATION;
    }
}

package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.entity.living.ChestbursterDraco;

public class ChestbursterDracoModel extends GeoModel<ChestbursterDraco> {

    private static final String ENTITY_NAME = "chestburster_draco";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(ChestbursterDraco entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(ChestbursterDraco entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(ChestbursterDraco entity) {
        return ANIMATION_LOCATION;
    }

    @Override
    public void setCustomAnimations(ChestbursterDraco entity, long instanceId, AnimationState<ChestbursterDraco> animationState) {
        // TODO:
    }
}

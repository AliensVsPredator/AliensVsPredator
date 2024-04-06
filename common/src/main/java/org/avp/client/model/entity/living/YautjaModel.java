package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.resources.ResourceLocation;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.AVPResources;
import org.avp.common.entity.living.Yautja;

public class YautjaModel extends GeoModel<Yautja> {

    private static final String ENTITY_NAME = "yautja";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(Yautja entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Yautja entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(Yautja entity) {
        return ANIMATION_LOCATION;
    }

    @Override
    public void setCustomAnimations(Yautja entity, long instanceId, AnimationState<Yautja> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "face", -0.2F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0F
        );
    }
}

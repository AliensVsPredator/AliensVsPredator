package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.resources.ResourceLocation;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.AVPResources;
import org.avp.common.entity.living.Boiler;

/**
 * @author Boston Vanseghi
 */
public class BoilerModel extends GeoModel<Boiler> {

    private static final String ENTITY_NAME = "boiler";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(Boiler entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Boiler entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(Boiler entity) {
        return ANIMATION_LOCATION;
    }

    @Override
    public void setCustomAnimations(Boiler entity, long instanceId, AnimationState<Boiler> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.6F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            -1F,
            -0.38F
        );
    }
}

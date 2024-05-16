package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.resources.ResourceLocation;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.AVPResources;
import org.avp.common.entity.living.Dracomorph;

public class DracomorphModel extends GeoModel<Dracomorph> {

    private static final String ENTITY_NAME = "dracomorph";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.entityGeoLocation(ENTITY_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.entityTextureLocation(ENTITY_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.entityAnimationLocation(ENTITY_NAME);

    @Override
    public ResourceLocation getModelResource(Dracomorph entity) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(Dracomorph entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(Dracomorph entity) {
        return ANIMATION_LOCATION;
    }

    @Override
    public void setCustomAnimations(Dracomorph entity, long instanceId, AnimationState<Dracomorph> animationState) {
        // TODO: It shouldn't be the responsibility of the code to hide the eggSack by default.
        var eggSack = this.getAnimationProcessor().getBone("gSack1");
        eggSack.setHidden(true);

        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.6F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftThigh",
            "gRightThigh",
            -2F,
            0.38F
        );
    }
}

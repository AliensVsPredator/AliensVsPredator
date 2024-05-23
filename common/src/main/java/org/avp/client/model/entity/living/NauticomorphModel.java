package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Nauticomorph;

public class NauticomorphModel extends AVPGeoModel<Nauticomorph> {

    public NauticomorphModel() {
        super("nauticomorph", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Nauticomorph entity, long instanceId, AnimationState<Nauticomorph> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.2F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            -0.35F,
            0F
        );
    }
}

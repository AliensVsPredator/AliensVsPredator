package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Ultramorph;

public class UltramorphModel extends AVPGeoModel<Ultramorph> {

    public UltramorphModel() {
        super("ultramorph", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Ultramorph entity, long instanceId, AnimationState<Ultramorph> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gHead", 0.2F);
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

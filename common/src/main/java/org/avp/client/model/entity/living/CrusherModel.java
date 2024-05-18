package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Crusher;

public class CrusherModel extends AVPGeoModel<Crusher> {
    public CrusherModel() {
        super("crusher");
    }

    @Override
    public void setCustomAnimations(Crusher entity, long instanceId, AnimationState<Crusher> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -1.8F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            -0.38F,
            0.35F
        );
    }
}

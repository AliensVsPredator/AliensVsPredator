package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Praetorian;

public class PraetorianModel extends AVPGeoModel<Praetorian> {
    public PraetorianModel() {
        super("praetorian");
    }

    @Override
    public void setCustomAnimations(Praetorian entity, long instanceId, AnimationState<Praetorian> animationState) {
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

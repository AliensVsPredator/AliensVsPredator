package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.ChestbursterRunner;

public class ChestbursterRunnerModel extends AVPGeoModel<ChestbursterRunner> {

    public ChestbursterRunnerModel() {
        super("chestburster_runner", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(ChestbursterRunner entity, long instanceId, AnimationState<ChestbursterRunner> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.4F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0.35F
        );
    }
}

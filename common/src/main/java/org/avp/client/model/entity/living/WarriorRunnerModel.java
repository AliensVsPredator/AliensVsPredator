package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.WarriorRunner;

public class WarriorRunnerModel extends AVPGeoModel<WarriorRunner> {

    public WarriorRunnerModel() {
        super("warrior_runner", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(WarriorRunner entity, long instanceId, AnimationState<WarriorRunner> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.4F);
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

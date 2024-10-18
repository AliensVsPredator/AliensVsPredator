package org.avp.client.model.entity.living.alien.runner_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.runner_line.DroneRunner;

public class DroneRunnerModel extends AVPGeoModel<DroneRunner> {

    public DroneRunnerModel() {
        super("drone_runner", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(DroneRunner entity, long instanceId, AnimationState<DroneRunner> animationState) {
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

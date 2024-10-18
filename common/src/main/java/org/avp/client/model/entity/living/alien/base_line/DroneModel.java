package org.avp.client.model.entity.living.alien.base_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.base_line.Drone;

public class DroneModel extends AVPGeoModel<Drone> {

    public DroneModel() {
        super("drone", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Drone entity, long instanceId, AnimationState<Drone> animationState) {
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

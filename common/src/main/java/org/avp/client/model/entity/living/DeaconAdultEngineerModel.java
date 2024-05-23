package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.DeaconAdultEngineer;

public class DeaconAdultEngineerModel extends AVPGeoModel<DeaconAdultEngineer> {

    public DeaconAdultEngineerModel() {
        super("deacon_adult_engineer", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(DeaconAdultEngineer entity, long instanceId, AnimationState<DeaconAdultEngineer> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.4F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftShoulder",
            "gRightShoulder",
            "gLeftThigh",
            "gRightThigh",
            -0.35F,
            0.35F
        );
    }
}

package org.avp.client.model.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.deacon_line.Deacon;

public class DeaconModel extends AVPGeoModel<Deacon> {

    public DeaconModel() {
        super("deacon", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Deacon entity, long instanceId, AnimationState<Deacon> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.3F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftShoulder",
            "gRightShoulder",
            "gLeftLeg",
            "gRightLeg",
            -0.35F,
            0.35F
        );
    }
}

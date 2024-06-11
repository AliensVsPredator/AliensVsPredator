package org.avp.client.model.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.deacon_line.DeaconAdult;

public class DeaconAdultModel extends AVPGeoModel<DeaconAdult> {

    public DeaconAdultModel() {
        super("deacon_adult", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(DeaconAdult entity, long instanceId, AnimationState<DeaconAdult> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "neck", -0.3F);
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

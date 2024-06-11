package org.avp.client.model.entity.living.alien.base_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.base_line.Boiler;

public class BoilerModel extends AVPGeoModel<Boiler> {

    public BoilerModel() {
        super("boiler", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Boiler entity, long instanceId, AnimationState<Boiler> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gHead", 0.5F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            -1F,
            -0.38F
        );
    }
}

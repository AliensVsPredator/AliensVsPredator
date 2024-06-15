package org.avp.client.model.entity.living.engineer;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.engineer.Engineer;

public class EngineerModel extends AVPGeoModel<Engineer> {

    public EngineerModel() {
        super("engineer", GeoModelType.ENTITY);
    }

    public void showHelmet(boolean showHelmet) {
        var helmet = this.getAnimationProcessor().getBone("gHelmet");

        if (helmet != null) {
            helmet.setHidden(!showHelmet);
        }
    }

    @Override
    public void setCustomAnimations(Engineer entity, long instanceId, AnimationState<Engineer> animationState) {
        this.showHelmet(entity.hasHelmet());
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.2F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftThigh",
            "gRightThigh",
            0F,
            0F
        );
    }
}

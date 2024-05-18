package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Yautja;

public class YautjaModel extends AVPGeoModel<Yautja> {
    public YautjaModel() {
        super("yautja");
    }

    public void showHelmet(boolean showHelmet) {
        var helmet = this.getAnimationProcessor().getBone("gMaskArmor");

        if (helmet != null) {
            helmet.setHidden(!showHelmet);
        }
    }

    @Override
    public void setCustomAnimations(Yautja entity, long instanceId, AnimationState<Yautja> animationState) {
        this.showHelmet(entity.hasHelmet());
        BasicAnimationUtils.applyHeadRotations(this, animationState, "face", -0.2F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0F
        );
    }
}

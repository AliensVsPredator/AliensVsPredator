package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.Yautja;

public class YautjaModel extends AVPGeoModel<Yautja> {
    public YautjaModel() {
        super("yautja");
    }

    public void showHelmet(boolean show) {
        var helmet = this.getAnimationProcessor().getBone("gMaskArmor");

        if (helmet != null) {
            helmet.setHidden(!show);
        }
    }

    public void showWristblades(boolean show) {
        // FIXME: This is not the right group to hide, but it is close enough.
        var blades = this.getAnimationProcessor().getBone("gRightForearmArmor");

        if (blades != null) {
            blades.setHidden(!show);
        }
    }

    @Override
    public void setCustomAnimations(Yautja entity, long instanceId, AnimationState<Yautja> animationState) {
        this.showHelmet(entity.hasHelmet.get());
        this.showWristblades(entity.wristbladesVisible.get());
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

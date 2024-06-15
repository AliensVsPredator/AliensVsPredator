package org.avp.client.model.entity.living.yautja;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.yautja.Yautja;

public class YautjaModel extends AVPGeoModel<Yautja> {

    public YautjaModel() {
        super("yautja", GeoModelType.ENTITY);
    }

    public void showHelmet(boolean show) {
        var helmet = this.getAnimationProcessor().getBone("gMaskArmor");

        if (helmet != null) {
            helmet.setHidden(!show);
        }
    }

    public void showWristblades(boolean show) {
        var blades = this.getAnimationProcessor().getBone("gWristBlade");

        if (blades != null) {
            blades.setHidden(!show);
        }
    }

    @Override
    public void setCustomAnimations(Yautja entity, long instanceId, AnimationState<Yautja> animationState) {
        this.showHelmet(entity.hasHelmet.get());
        this.showWristblades(entity.wristbladesVisible.get());
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeckUpper", -0.2F);
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

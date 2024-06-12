package org.avp.client.model.entity.living.alien.draco_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.draco_line.Dracomorph;

public class DracomorphModel extends AVPGeoModel<Dracomorph> {

    public DracomorphModel() {
        super("dracomorph", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Dracomorph entity, long instanceId, AnimationState<Dracomorph> animationState) {
        // TODO: It shouldn't be the responsibility of the code to hide the eggSack by default.
        var eggSack = this.getAnimationProcessor().getBone("gSack1");
        eggSack.setHidden(true);

        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.6F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftThigh",
            "gRightThigh",
            -2F,
            0.38F
        );
    }
}

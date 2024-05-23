package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.ChestbursterQueen;

public class ChestbursterQueenModel extends AVPGeoModel<ChestbursterQueen> {

    public ChestbursterQueenModel() {
        super("chestburster_queen", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(ChestbursterQueen entity, long instanceId, AnimationState<ChestbursterQueen> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.4F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            1.15F,
            0.75F
        );
    }
}

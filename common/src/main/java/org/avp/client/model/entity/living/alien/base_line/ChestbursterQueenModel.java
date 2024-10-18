package org.avp.client.model.entity.living.alien.base_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.base_line.ChestbursterQueen;

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

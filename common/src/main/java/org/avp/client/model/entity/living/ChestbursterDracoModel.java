package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.entity.living.ChestbursterDraco;

public class ChestbursterDracoModel extends AVPGeoModel<ChestbursterDraco> {
    public ChestbursterDracoModel() {
        super("chestburster_draco", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(ChestbursterDraco entity, long instanceId, AnimationState<ChestbursterDraco> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", 0.4F);
        BasicAnimationUtils.applyLimbRotations(
            entity,
            this,
            animationState,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0.35F
        );
    }
}

package org.avp.client.model.entity.living.alien.beluga_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.client.util.BasicAnimationUtils;
import org.avp.common.game.entity.living.alien.beluga_line.Belugamorph;

public class BelugamorphModel extends AVPGeoModel<Belugamorph> {

    public BelugamorphModel() {
        super("belugamorph", GeoModelType.ENTITY);
    }

    @Override
    public void setCustomAnimations(Belugamorph entity, long instanceId, AnimationState<Belugamorph> animationState) {
        BasicAnimationUtils.applyHeadRotations(this, animationState, "gNeck", -0.2F);
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

package com.blib.azurelib.common.animation.event;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.core.keyframe.event.data.ParticleKeyframeData;

public class AzParticleKeyframeEvent<T> extends AzKeyframeEvent<T, ParticleKeyframeData> {

    public AzParticleKeyframeEvent(
        T animatable,
        double animationTick,
        AzAnimationController<T> controller,
        ParticleKeyframeData particleKeyframeData
    ) {
        super(animatable, animationTick, controller, particleKeyframeData);
    }

    @Override
    public ParticleKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}

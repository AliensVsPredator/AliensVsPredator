package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.api.client.animation.v1.keyframe.data.ParticleKeyframeData;

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

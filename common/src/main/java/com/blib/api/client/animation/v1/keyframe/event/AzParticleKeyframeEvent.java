package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.keyframe.data.ParticleKeyframeData;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;

public class AzParticleKeyframeEvent<T> extends AzKeyframeEvent<T, ParticleKeyframeData> {

    public AzParticleKeyframeEvent(
        T animatable,
        double animationTick,
        AzAnimationTrack<T> track,
        ParticleKeyframeData particleKeyframeData
    ) {
        super(animatable, animationTick, track, particleKeyframeData);
    }

    @Override
    public ParticleKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}

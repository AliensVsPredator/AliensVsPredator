package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.keyframe.data.KeyFrameData;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;

public abstract class AzKeyframeEvent<T, E extends KeyFrameData> {

    private final T animatable;

    private final double animationTick;

    private final AzAnimationTrack<T> track;

    private final E eventKeyframe;

    protected AzKeyframeEvent(
        T animatable,
        double animationTick,
        AzAnimationTrack<T> track,
        E eventKeyframe
    ) {
        this.animatable = animatable;
        this.animationTick = animationTick;
        this.track = track;
        this.eventKeyframe = eventKeyframe;
    }

    public double getAnimationTick() {
        return animationTick;
    }

    public T getAnimatable() {
        return animatable;
    }

    public AzAnimationTrack<T> getTrack() {
        return track;
    }

    public E getKeyframeData() {
        return this.eventKeyframe;
    }
}

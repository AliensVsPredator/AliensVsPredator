package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.api.client.animation.v1.keyframe.data.KeyFrameData;

public abstract class AzKeyframeEvent<T, E extends KeyFrameData> {

    private final T animatable;

    private final double animationTick;

    private final AzAnimationController<T> controller;

    private final E eventKeyframe;

    protected AzKeyframeEvent(
        T animatable,
        double animationTick,
        AzAnimationController<T> controller,
        E eventKeyframe
    ) {
        this.animatable = animatable;
        this.animationTick = animationTick;
        this.controller = controller;
        this.eventKeyframe = eventKeyframe;
    }

    public double getAnimationTick() {
        return animationTick;
    }

    public T getAnimatable() {
        return animatable;
    }

    public AzAnimationController<T> getController() {
        return controller;
    }

    public E getKeyframeData() {
        return this.eventKeyframe;
    }
}

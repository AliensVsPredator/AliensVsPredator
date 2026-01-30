package com.blib.internal.client.animation;

import com.blib.internal.client.animation.cache.AzBoneCache;

public class AzAnimationContext<T> {

    private final AzBoneCache boneCache;

    private final AzAnimatorConfig config;

    private final AzAnimationTimer timer;

    // Package-private for mutability purposes.
    private T animatable;

    public AzAnimationContext(
        AzBoneCache boneCache,
        AzAnimatorConfig config,
        AzAnimationTimer timer
    ) {
        this.boneCache = boneCache;
        this.config = config;
        this.timer = timer;
    }

    public T animatable() {
        return animatable;
    }

    public void setAnimatable(T animatable) {
        this.animatable = animatable;
    }

    public AzBoneCache boneCache() {
        return boneCache;
    }

    public AzAnimatorConfig config() {
        return config;
    }

    public AzAnimationTimer timer() {
        return timer;
    }
}

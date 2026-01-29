package com.blib.azurelib.common.animation.controller;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.controller.keyframe.AzKeyframeCallbacks;
import com.blib.azurelib.common.animation.easing.AzEasingType;
import com.blib.azurelib.common.animation.property.AzAnimationProperties;

public class AzAnimationControllerBuilder<T> {

    private final AzAnimator<?, T> animator;

    private final String name;

    private AzAnimationProperties animationProperties;

    private AzKeyframeCallbacks<T> keyframeCallbacks;

    public AzAnimationControllerBuilder(AzAnimator<?, T> animator, String name) {
        this.animator = animator;
        this.name = name;
        this.animationProperties = AzAnimationProperties.DEFAULT;
        this.keyframeCallbacks = AzKeyframeCallbacks.noop();
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeed(double animationSpeed) {
        animationProperties = animationProperties.withAnimationSpeed(animationSpeed);
        return this;
    }

    public AzAnimationControllerBuilder<T> setKeyframeCallbacks(@NotNull AzKeyframeCallbacks<T> keyframeCallbacks) {
        Objects.requireNonNull(keyframeCallbacks);
        this.keyframeCallbacks = keyframeCallbacks;
        return this;
    }

    public AzAnimationControllerBuilder<T> setEasingType(AzEasingType easingType) {
        animationProperties = animationProperties.withEasingType(easingType);
        return this;
    }

    public AzAnimationControllerBuilder<T> setTransitionLength(int transitionLength) {
        animationProperties = animationProperties.withTransitionLength(transitionLength);
        return this;
    }

    public AzAnimationControllerBuilder<T> setStartTickOffset(double startTickOffset) {
        animationProperties = animationProperties.withStartTickOffset(startTickOffset);
        return this;
    }

    public AzAnimationController<T> build() {
        return new AzAnimationController<>(
            name,
            animator,
            animationProperties,
            keyframeCallbacks
        );
    }
}

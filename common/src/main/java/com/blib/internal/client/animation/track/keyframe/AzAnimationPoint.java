package com.blib.internal.client.animation.track.keyframe;

public record AzAnimationPoint(
    AzKeyframe<?> keyframe,
    double currentTick,
    double transitionLength,
    double animationStartValue,
    double animationEndValue
) {

    @Override
    public String toString() {
        return "Tick: " + this.currentTick +
            " | Transition Length: " + this.transitionLength +
            " | Start Value: " + this.animationStartValue +
            " | End Value: " + this.animationEndValue;
    }
}

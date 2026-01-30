package com.blib.internal.client.animation.controller.keyframe;

public record AzKeyframeLocation<T extends AzKeyframe<?>>(
    T keyframe,
    double startTick
) {}

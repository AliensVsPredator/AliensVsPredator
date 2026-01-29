package com.blib.azurelib.common.animation.controller.keyframe;

public record AzKeyframeLocation<T extends AzKeyframe<?>>(
    T keyframe,
    double startTick
) {}

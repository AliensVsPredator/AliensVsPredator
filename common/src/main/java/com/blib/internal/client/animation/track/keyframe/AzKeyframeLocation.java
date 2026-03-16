package com.blib.internal.client.animation.track.keyframe;

public record AzKeyframeLocation<T extends AzKeyframe<?>>(
    T keyframe,
    double startTick
) {}

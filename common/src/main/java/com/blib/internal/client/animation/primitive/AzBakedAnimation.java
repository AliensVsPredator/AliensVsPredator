package com.blib.internal.client.animation.primitive;

import com.blib.internal.client.animation.track.keyframe.AzBoneAnimation;

public record AzBakedAnimation(
    String name,
    double length,
    AzLoopType loopType,
    AzBoneAnimation[] boneAnimations,
    AzKeyframes keyframes
) {

}

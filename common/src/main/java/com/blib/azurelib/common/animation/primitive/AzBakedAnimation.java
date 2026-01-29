package com.blib.azurelib.common.animation.primitive;

import com.blib.azurelib.common.animation.controller.keyframe.AzBoneAnimation;

public record AzBakedAnimation(
    String name,
    double length,
    AzLoopType loopType,
    AzBoneAnimation[] boneAnimations,
    AzKeyframes keyframes
) {

}

package com.blib.azurelib.common.animation.controller.keyframe;

import com.blib.azurelib.core.math.IValue;

public record AzBoneAnimation(
    String boneName,
    AzKeyframeStack<AzKeyframe<IValue>> rotationKeyframes,
    AzKeyframeStack<AzKeyframe<IValue>> positionKeyframes,
    AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes
) {}

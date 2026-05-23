package com.blib.internal.client.animation.track.keyframe;

import com.blib.internal.common.molang.math.IValue;

public record AzBoneAnimation(
    String boneName,
    AzKeyframeStack<AzKeyframe<IValue>> rotationKeyframes,
    AzKeyframeStack<AzKeyframe<IValue>> positionKeyframes,
    AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes
) {}

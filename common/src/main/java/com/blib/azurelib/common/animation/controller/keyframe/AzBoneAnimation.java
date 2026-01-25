package com.blib.azurelib.common.animation.controller.keyframe;

import com.blib.azurelib.core.math.IValue;

/**
 * A record of a deserialized animation for a given bone.<br>
 * Responsible for holding the various {@link com.blib.azurelib.common.animation.controller.keyframe.AzKeyframe
 * Keyframes} for the bone's animation transformations
 *
 * @param boneName          The name of the bone as listed in the {@code animation.json}
 * @param rotationKeyframes The deserialized rotation {@code Keyframe} stack
 * @param positionKeyframes The deserialized position {@code Keyframe} stack
 * @param scaleKeyframes    The deserialized scale {@code Keyframe} stack
 */
public record AzBoneAnimation(
    String boneName,
    AzKeyframeStack<com.blib.azurelib.common.animation.controller.keyframe.AzKeyframe<IValue>> rotationKeyframes,
    AzKeyframeStack<com.blib.azurelib.common.animation.controller.keyframe.AzKeyframe<IValue>> positionKeyframes,
    AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes
) {}

package com.blib.internal.client.animation.primitive;

import com.blib.api.client.animation.v1.keyframe.data.CustomInstructionKeyframeData;
import com.blib.api.client.animation.v1.keyframe.data.ParticleKeyframeData;
import com.blib.api.client.animation.v1.keyframe.data.SoundKeyframeData;

public record AzKeyframes(
    SoundKeyframeData[] sounds,
    ParticleKeyframeData[] particles,
    CustomInstructionKeyframeData[] customInstructions
) {}

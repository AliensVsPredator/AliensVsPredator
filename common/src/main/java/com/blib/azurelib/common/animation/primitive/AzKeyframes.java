package com.blib.azurelib.common.animation.primitive;

import com.blib.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import com.blib.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import com.blib.azurelib.core.keyframe.event.data.SoundKeyframeData;

public record AzKeyframes(
    SoundKeyframeData[] sounds,
    ParticleKeyframeData[] particles,
    CustomInstructionKeyframeData[] customInstructions
) {}

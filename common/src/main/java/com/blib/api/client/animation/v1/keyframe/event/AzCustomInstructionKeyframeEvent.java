package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.keyframe.data.CustomInstructionKeyframeData;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;

public class AzCustomInstructionKeyframeEvent<T> extends AzKeyframeEvent<T, CustomInstructionKeyframeData> {

    public AzCustomInstructionKeyframeEvent(
        T entity,
        double animationTick,
        AzAnimationTrack<T> track,
        CustomInstructionKeyframeData customInstructionKeyframeData
    ) {
        super(entity, animationTick, track, customInstructionKeyframeData);
    }

    @Override
    public CustomInstructionKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}

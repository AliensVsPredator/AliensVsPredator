package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.api.client.animation.v1.keyframe.data.SoundKeyframeData;

public class AzSoundKeyframeEvent<T> extends AzKeyframeEvent<T, SoundKeyframeData> {

    public AzSoundKeyframeEvent(
        T entity,
        double animationTick,
        AzAnimationController<T> controller,
        SoundKeyframeData keyframeData
    ) {
        super(entity, animationTick, controller, keyframeData);
    }
}

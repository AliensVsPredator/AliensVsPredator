package com.blib.azurelib.common.animation.event;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.core.keyframe.event.data.SoundKeyframeData;

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

package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.api.client.animation.v1.keyframe.data.CustomInstructionKeyframeData;

public class AzCustomInstructionKeyframeEvent<T> extends AzKeyframeEvent<T, CustomInstructionKeyframeData> {

    public AzCustomInstructionKeyframeEvent(
        T entity,
        double animationTick,
        AzAnimationController<T> controller,
        CustomInstructionKeyframeData customInstructionKeyframeData
    ) {
        super(entity, animationTick, controller, customInstructionKeyframeData);
    }

    @Override
    public CustomInstructionKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}

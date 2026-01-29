package com.blib.azurelib.common.animation.event;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;

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

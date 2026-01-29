package com.blib.azurelib.common.animation.controller.keyframe.handler;

import com.blib.azurelib.common.animation.event.AzCustomInstructionKeyframeEvent;

@FunctionalInterface
public interface AzCustomKeyframeHandler<A> {

    void handle(AzCustomInstructionKeyframeEvent<A> event);
}

package com.blib.api.client.animation.v1.keyframe.handler;

import com.blib.api.client.animation.v1.keyframe.event.AzCustomInstructionKeyframeEvent;

@FunctionalInterface
public interface AzCustomKeyframeHandler<A> {

    void handle(AzCustomInstructionKeyframeEvent<A> event);
}

package com.blib.azurelib.common.animation.controller.keyframe.handler;

import com.blib.azurelib.common.animation.event.AzParticleKeyframeEvent;

@FunctionalInterface
public interface AzParticleKeyframeHandler<A> {

    void handle(AzParticleKeyframeEvent<A> event);
}

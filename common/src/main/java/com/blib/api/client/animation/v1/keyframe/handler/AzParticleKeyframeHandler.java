package com.blib.api.client.animation.v1.keyframe.handler;

import com.blib.api.client.animation.v1.keyframe.event.AzParticleKeyframeEvent;

@FunctionalInterface
public interface AzParticleKeyframeHandler<A> {

    void handle(AzParticleKeyframeEvent<A> event);
}

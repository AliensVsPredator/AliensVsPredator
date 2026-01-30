package com.blib.api.client.animation.v1.keyframe.handler;

import com.blib.api.client.animation.v1.keyframe.event.AzSoundKeyframeEvent;

@FunctionalInterface
public interface AzSoundKeyframeHandler<A> {

    void handle(AzSoundKeyframeEvent<A> event);
}

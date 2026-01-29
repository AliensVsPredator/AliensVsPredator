package com.blib.azurelib.common.animation.controller.keyframe.handler;

import com.blib.azurelib.common.animation.event.AzSoundKeyframeEvent;

@FunctionalInterface
public interface AzSoundKeyframeHandler<A> {

    void handle(AzSoundKeyframeEvent<A> event);
}

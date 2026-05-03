package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzSetStartTickOffsetAction(
    AzTarget target,
    double startTickOffset
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.setAnimationProperties(
                track.animationProperties().withStartTickOffset(startTickOffset)
            )
        );
    }
}

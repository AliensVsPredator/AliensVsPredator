package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzSetFreezeTickAction(
    AzTarget target,
    double freezeTickOffset
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.setAnimationProperties(
                track.animationProperties().withFreezeTickOffset(freezeTickOffset)
            )
        );
    }
}

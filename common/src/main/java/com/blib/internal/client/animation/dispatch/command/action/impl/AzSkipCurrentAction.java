package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Drops the current animation. The queue is preserved.
 */
public record AzSkipCurrentAction<T>(AzTarget target) implements AzAction<T> {

    @Override
    public void handle(AzAnimator<?, T> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.setCurrentAnimation(null)
        );
    }
}

package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Full cancel: clears the current animation, drains the queue, and transitions the state machine to
 * STOP.
 */
public record AzCancelAction<T>(AzTarget target) implements AzAction<T> {

    @Override
    public void handle(AzAnimator<?, T> animator) {
        target.forEach(animator.getAnimationTrackContainer(), track -> {
            track.setCurrentAnimation(null);
            track.animationQueue().clear();
            track.stateMachine().stop();
        });
    }
}

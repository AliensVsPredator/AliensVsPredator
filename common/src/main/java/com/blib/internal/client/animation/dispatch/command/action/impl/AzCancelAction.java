package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Full cancel: clears the current animation, drains the queue, and transitions the state machine to
 * STOP. The track ends up silent and stays silent until something dispatches a new play action.
 *
 * <p>For "drop the current animation but let the queue keep playing," use
 * {@link AzSkipCurrentAction} instead.</p>
 */
public record AzCancelAction(AzTarget target) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(animator.getAnimationTrackContainer(), track -> {
            track.setCurrentAnimation(null);
            track.animationQueue().clear();
            track.stateMachine().stop();
        });
    }
}

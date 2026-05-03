package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Drops the current animation on each target track. The queue is preserved — on the next state
 * machine tick, the next queued animation begins playing. If the queue is empty, the state machine
 * transitions to STOP via the play state's standard end-of-animation flow.
 *
 * <p>For an immediate full stop (clear queue, no auto-resume), use {@link AzCancelAction}.</p>
 */
public record AzSkipCurrentAction(AzTarget target) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.setCurrentAnimation(null)
        );
    }
}

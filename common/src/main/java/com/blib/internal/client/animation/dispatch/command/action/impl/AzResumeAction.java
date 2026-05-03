package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Unfreezes a paused animation on each target track, returning it to the PLAY state. No-ops on
 * tracks that are not currently paused.
 */
public record AzResumeAction(AzTarget target) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(animator.getAnimationTrackContainer(), track -> {
            var stateMachine = track.stateMachine();

            if (stateMachine.isPaused()) {
                stateMachine.play();
            }
        });
    }
}

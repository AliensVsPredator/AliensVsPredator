package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Freezes the current animation on each target track at its current frame. No-ops on tracks that
 * are already stopped (nothing to pause).
 */
public record AzPauseAction(AzTarget target) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(animator.getAnimationTrackContainer(), track -> {
            var stateMachine = track.stateMachine();

            if (!stateMachine.isStopped() && !stateMachine.isPaused()) {
                stateMachine.pause();
            }
        });
    }
}

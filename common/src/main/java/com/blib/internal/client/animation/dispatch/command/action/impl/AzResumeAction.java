package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

/**
 * Unfreezes a paused animation on each target track. Pre-checks {@code isPaused()} so the action's
 * contract is "only resume from PAUSE" — no-op silently on tracks that aren't currently paused.
 * The PAUSE → PLAY transition is always legal, so the result of the underlying state machine call
 * is {@link com.blib.internal.client.animation.track.state.AzTransitionResult.Applied} on success.
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

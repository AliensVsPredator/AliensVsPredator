package com.blib.internal.client.animation.dispatch.command.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.BLibAPI;
import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.internal.client.animation.track.state.AzTransitionResult;

/**
 * Freezes the current animation on each target track. The state machine's legal-transition guard
 * means pause is only legal from PLAY (and a no-op from PAUSE). On STOP or TRANSITION the state
 * machine returns {@link AzTransitionResult.Rejected}; in dev this throws, in prod it logs.
 */
public record AzPauseAction(AzTarget target) implements AzAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzPauseAction.class);

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(animator.getAnimationTrackContainer(), track -> {
            var result = track.stateMachine().pause();

            if (result instanceof AzTransitionResult.Rejected r) {
                handleRejection(track, r);
            }
        });
    }

    private void handleRejection(AzAnimationTrack<?> track, AzTransitionResult.Rejected rejected) {
        var message = "Pause rejected on track '%s': cannot pause from %s"
            .formatted(track.name(), rejected.from());

        if (BLibAPI.isDevelopmentEnvironment()) {
            throw new IllegalStateException(message);
        }

        LOGGER.warn(message);
    }
}

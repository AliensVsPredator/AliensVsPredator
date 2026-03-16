package com.blib.internal.client.animation.dispatch.command.action.impl.track;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzTrackSetAnimationSpeedAction(
    String trackName,
    double animationSpeed
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        var track = animator.getAnimationTrackContainer().getOrNull(trackName);

        if (track != null) {
            track.setAnimationProperties(track.animationProperties().withAnimationSpeed(animationSpeed));
        }
    }
}

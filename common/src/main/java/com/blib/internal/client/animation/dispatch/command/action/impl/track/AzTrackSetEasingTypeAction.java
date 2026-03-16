package com.blib.internal.client.animation.dispatch.command.action.impl.track;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.internal.client.animation.easing.AzEasingType;

public record AzTrackSetEasingTypeAction(
    String trackName,
    AzEasingType easingType
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        var track = animator.getAnimationTrackContainer().getOrNull(trackName);

        if (track != null) {
            track.setAnimationProperties(track.animationProperties().withEasingType(easingType));
        }
    }
}

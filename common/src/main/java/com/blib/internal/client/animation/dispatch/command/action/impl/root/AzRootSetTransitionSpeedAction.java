package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzRootSetTransitionSpeedAction(
    float transitionSpeed
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        animator.getAnimationTrackContainer()
            .getAll()
            .forEach(
                track -> track.setAnimationProperties(
                    track.animationProperties().withTransitionLength(transitionSpeed)
                )
            );
    }
}

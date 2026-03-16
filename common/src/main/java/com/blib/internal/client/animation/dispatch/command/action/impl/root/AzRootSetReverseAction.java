package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzRootSetReverseAction(
    boolean hasReverse
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        animator.getAnimationTrackContainer()
            .getAll()
            .forEach(
                track -> track.setAnimationProperties(
                    track.animationProperties().withShouldReverse(hasReverse)
                )
            );
    }
}

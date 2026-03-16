package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzRootSetFreezeTickAction(
    double freezeTickOffset
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        animator.getAnimationTrackContainer()
            .getAll()
            .forEach(
                track -> track.setAnimationProperties(
                    track.animationProperties().withFreezeTickOffset(freezeTickOffset)
                )
            );
    }
}

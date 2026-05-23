package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzPlayAnimationSequenceAction<T>(
    AzTarget target,
    AzAnimationSequence sequence,
    AzDispatchPolicy policy
) implements AzAction<T> {

    @Override
    public void handle(AzAnimator<?, T> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.run(sequence, policy)
        );
    }
}

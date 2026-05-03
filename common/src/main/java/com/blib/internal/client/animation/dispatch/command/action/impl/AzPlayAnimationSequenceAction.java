package com.blib.internal.client.animation.dispatch.command.action.impl;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.AzTarget;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzPlayAnimationSequenceAction(
    AzTarget target,
    AzAnimationSequence sequence,
    AzDispatchPolicy policy
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        target.forEach(
            animator.getAnimationTrackContainer(),
            track -> track.run(sequence, policy)
        );
    }
}

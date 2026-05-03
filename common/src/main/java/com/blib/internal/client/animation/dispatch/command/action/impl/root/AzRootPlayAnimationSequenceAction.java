package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzRootPlayAnimationSequenceAction(
    AzAnimationSequence sequence,
    AzDispatchPolicy policy
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        var trackContainer = animator.getAnimationTrackContainer();
        var tracks = trackContainer.getAll();

        tracks.forEach(track -> track.run(sequence, policy));
    }
}

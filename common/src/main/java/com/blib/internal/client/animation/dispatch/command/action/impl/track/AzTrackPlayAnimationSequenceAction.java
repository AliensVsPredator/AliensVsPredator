package com.blib.internal.client.animation.dispatch.command.action.impl.track;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzTrackPlayAnimationSequenceAction(
    String trackName,
    AzAnimationSequence sequence,
    AzDispatchPolicy policy
) implements AzAction {

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        var track = animator.getAnimationTrackContainer().getOrNull(trackName);

        if (track != null) {
            track.run(sequence, policy);
        }
    }
}

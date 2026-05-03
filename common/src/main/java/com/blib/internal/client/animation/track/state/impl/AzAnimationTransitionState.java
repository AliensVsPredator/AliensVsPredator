package com.blib.internal.client.animation.track.state.impl;

import com.blib.internal.client.animation.track.state.AzAnimationState;
import com.blib.internal.client.animation.track.state.AzAnimationStateKind;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public final class AzAnimationTransitionState<T> extends AzAnimationState<T> {

    public AzAnimationTransitionState() {}

    @Override
    public AzAnimationStateKind kind() {
        return AzAnimationStateKind.TRANSITION;
    }

    @Override
    public void onEnter(AzAnimationTrackStateMachine.Context<T> context) {
        super.onEnter(context);
        prepareTransition(context);
    }

    @Override
    public void onUpdate(AzAnimationTrackStateMachine.Context<T> context) {
        var track = context.animationTrack();
        var trackTimer = track.trackTimer();
        var animContext = context.animationContext();

        var stateMachine = context.stateMachine();
        var boneCache = animContext.boneCache();

        var transitionLength = track.animationProperties().transitionLength();
        var hasFinishedTransitioning = trackTimer.getAdjustedTick() >= transitionLength;

        if (hasFinishedTransitioning) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.
            stateMachine.play();
            return;
        }

        if (track.currentAnimation() != null) {
            var bones = boneCache.getBakedModel().getBonesByName();
            var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            var keyframeTransitioner = track.keyframeManager().keyframeTransitioner();

            keyframeTransitioner.transition(bones, crashWhenCantFindBone, trackTimer.getAdjustedTick());
        }
    }

    private void prepareTransition(AzAnimationTrackStateMachine.Context<?> context) {
        var animContext = context.animationContext();
        var boneCache = animContext.boneCache();
        var track = context.animationTrack();
        var boneSnapshotCache = track.boneSnapshotCache();
        var trackTimer = track.trackTimer();

        trackTimer.reset();
        track.keyframeManager().keyframeCallbackHandler().reset();

        var nextAnimation = track.animationQueue().next();

        if (nextAnimation == null) {
            return;
        }

        track.setCurrentAnimation(nextAnimation);

        var snapshots = boneCache.getBoneSnapshotsByName();

        boneSnapshotCache.put(nextAnimation, snapshots.values());
    }
}

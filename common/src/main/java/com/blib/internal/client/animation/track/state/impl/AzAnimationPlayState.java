package com.blib.internal.client.animation.track.state.impl;

import com.blib.internal.client.animation.track.state.AzAnimationState;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public class AzAnimationPlayState<T> extends AzAnimationState<T> {

    public AzAnimationPlayState() {}

    @Override
    public void onEnter(AzAnimationTrackStateMachine.Context<T> context) {
        super.onEnter(context);
        var track = context.animationTrack();
        var trackTimer = track.trackTimer();

        trackTimer.reset();
    }

    @Override
    public void onUpdate(AzAnimationTrackStateMachine.Context<T> context) {
        var track = context.animationTrack();
        var trackTimer = track.trackTimer();
        var currentAnimation = track.currentAnimation();

        if (currentAnimation == null) {
            // If the current animation is null, we should try to play the next animation.
            tryPlayNextOrStop(context);
            return;
        }

        currentAnimation.playBehavior().onUpdate(context);

        // At this point we have an animation currently playing. We need to query if that animation has finished.

        var animContext = context.animationContext();
        var animatable = animContext.animatable();
        var hasAnimationFinished = trackTimer.getAdjustedTick() >= currentAnimation.animation().length();

        if (hasAnimationFinished) {
            currentAnimation.playBehavior().onFinish(context);
        }

        if (context.stateMachine().isStopped()) {
            // Nothing more to do at this point since we can't play the animation again, so return.
            return;
        }

        // The animation is still running at this point, proceed with updating the bones according to keyframes.

        var keyframeManager = track.keyframeManager();
        var keyframeExecutor = keyframeManager.keyframeExecutor();
        var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();

        keyframeExecutor.execute(currentAnimation, animatable, crashWhenCantFindBone);
    }

    private void tryPlayNextOrStop(AzAnimationTrackStateMachine.Context<T> context) {
        var track = context.animationTrack();
        var stateMachine = context.stateMachine();
        var keyframeManager = track.keyframeManager();
        var keyframeCallbackHandler = keyframeManager.keyframeCallbackHandler();

        keyframeCallbackHandler.reset();

        var animationQueue = track.animationQueue();
        var nextAnimation = animationQueue.peek();

        if (nextAnimation == null) {
            // If we can't play the next animation for some reason, then there's nothing to play.
            // So we should put the state machine in the 'stop' state.
            stateMachine.stop();
            return;
        }

        // If we can play the next animation successfully, then let's do that.
        stateMachine.transition();
        track.setCurrentAnimation(nextAnimation);
    }
}

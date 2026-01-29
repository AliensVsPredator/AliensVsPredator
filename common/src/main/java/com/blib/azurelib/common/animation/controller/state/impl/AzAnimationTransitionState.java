package com.blib.azurelib.common.animation.controller.state.impl;

import com.blib.azurelib.common.animation.controller.state.AzAnimationState;
import com.blib.azurelib.common.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationTransitionState<T> extends AzAnimationState<T> {

    public AzAnimationTransitionState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
        prepareTransition(context);
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.animationController();
        var controllerTimer = controller.controllerTimer();
        var animContext = context.animationContext();

        var stateMachine = context.stateMachine();
        var boneCache = animContext.boneCache();

        var transitionLength = controller.animationProperties().transitionLength();
        var hasFinishedTransitioning = controllerTimer.getAdjustedTick() >= transitionLength;

        if (hasFinishedTransitioning) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.
            stateMachine.play();
            return;
        }

        if (controller.currentAnimation() != null) {
            var bones = boneCache.getBakedModel().getBonesByName();
            var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            var keyframeTransitioner = controller.keyframeManager().keyframeTransitioner();

            keyframeTransitioner.transition(bones, crashWhenCantFindBone, controllerTimer.getAdjustedTick());
        }
    }

    private void prepareTransition(AzAnimationControllerStateMachine.Context<?> context) {
        var animContext = context.animationContext();
        var boneCache = animContext.boneCache();
        var controller = context.animationController();
        var boneSnapshotCache = controller.boneSnapshotCache();
        var controllerTimer = controller.controllerTimer();

        controllerTimer.reset();
        controller.keyframeManager().keyframeCallbackHandler().reset();

        var nextAnimation = controller.animationQueue().next();

        if (nextAnimation == null) {
            return;
        }

        controller.setCurrentAnimation(nextAnimation);

        var snapshots = boneCache.getBoneSnapshotsByName();

        boneSnapshotCache.put(nextAnimation, snapshots.values());
    }
}

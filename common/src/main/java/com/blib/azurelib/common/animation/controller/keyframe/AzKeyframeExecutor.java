package com.blib.azurelib.common.animation.controller.keyframe;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.controller.AzBoneAnimationQueueCache;
import com.blib.azurelib.common.animation.primitive.AzQueuedAnimation;
import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.molang.MolangParser;
import com.blib.azurelib.core.molang.MolangQueries;
import com.blib.azurelib.core.object.Axis;

public class AzKeyframeExecutor<T> extends AzAbstractKeyframeExecutor {

    private final AzAnimationController<T> animationController;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    public AzKeyframeExecutor(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache
    ) {
        this.animationController = animationController;
        this.boneAnimationQueueCache = boneAnimationQueueCache;
    }

    public void execute(@NotNull AzQueuedAnimation currentAnimation, T animatable, boolean crashWhenCantFindBone) {
        var keyframeCallbackHandler = animationController.keyframeManager().keyframeCallbackHandler();
        var controllerTimer = animationController.controllerTimer();

        final double finalAdjustedTick = controllerTimer.getAdjustedTick();

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone) {
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());
                }

                continue;
            }

            var rotationKeyframes = boneAnimation.rotationKeyframes();
            var positionKeyframes = boneAnimation.positionKeyframes();
            var scaleKeyframes = boneAnimation.scaleKeyframes();
            var adjustedTick = controllerTimer.getAdjustedTick();

            updateRotation(rotationKeyframes, boneAnimationQueue, adjustedTick);
            updatePosition(positionKeyframes, boneAnimationQueue, adjustedTick);
            updateScale(scaleKeyframes, boneAnimationQueue, adjustedTick);
        }

        keyframeCallbackHandler.handle(animatable, controllerTimer.getAdjustedTick());
    }

    private void updateRotation(
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyframes.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyframes.xKeyframes(), adjustedTick, true, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), adjustedTick, true, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), adjustedTick, true, Axis.Z);

        queue.addRotations(x, y, z);
    }

    private void updatePosition(
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyframes.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyframes.xKeyframes(), adjustedTick, false, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), adjustedTick, false, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), adjustedTick, false, Axis.Z);

        queue.addPositions(x, y, z);
    }

    private void updateScale(
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyframes.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyframes.xKeyframes(), adjustedTick, false, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), adjustedTick, false, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), adjustedTick, false, Axis.Z);

        queue.addScales(x, y, z);
    }
}

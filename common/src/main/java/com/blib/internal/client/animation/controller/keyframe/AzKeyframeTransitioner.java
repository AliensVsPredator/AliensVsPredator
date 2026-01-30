package com.blib.internal.client.animation.controller.keyframe;

import java.util.Map;
import java.util.NoSuchElementException;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.internal.client.animation.controller.AzBoneAnimationQueueCache;
import com.blib.internal.client.animation.controller.AzBoneSnapshotCache;
import com.blib.internal.client.model.AzBone;
import com.blib.internal.client.model.AzBoneSnapshot;
import com.blib.internal.common.model.Axis;
import com.blib.internal.common.molang.MolangParser;
import com.blib.internal.common.molang.MolangQueries;
import com.blib.internal.common.molang.math.IValue;

public class AzKeyframeTransitioner<T> extends AzAbstractKeyframeExecutor {

    private final AzAnimationController<T> animationController;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    public AzKeyframeTransitioner(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache,
        AzBoneSnapshotCache boneSnapshotCache
    ) {
        this.animationController = animationController;
        this.boneAnimationQueueCache = boneAnimationQueueCache;
        this.boneSnapshotCache = boneSnapshotCache;
    }

    public void transition(Map<String, AzBone> bones, boolean crashWhenCantFindBone, double adjustedTick) {
        var currentAnimation = animationController.currentAnimation();
        var transitionLength = animationController.animationProperties().transitionLength();
        adjustedTick = Math.min(adjustedTick, transitionLength); // Cap tick length

        MolangParser.INSTANCE.setValue(MolangQueries.ANIM_TIME, () -> 0);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var bone = bones.get(boneAnimation.boneName());

            if (bone == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var queue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());
            var snapshot = boneSnapshotCache.getOrNull(boneAnimation.boneName());

            if (snapshot == null || queue == null) {
                return;
            }

            var rotationKeyframes = boneAnimation.rotationKeyframes();
            var positionKeyframes = boneAnimation.positionKeyframes();
            var scaleKeyframes = boneAnimation.scaleKeyframes();

            transitionRotation(adjustedTick, rotationKeyframes, queue, transitionLength, snapshot, bone);
            transitionPosition(adjustedTick, positionKeyframes, queue, transitionLength, snapshot);
            transitionScale(adjustedTick, scaleKeyframes, queue, transitionLength, snapshot);
        }
    }

    private void transitionRotation(
        double adjustedTick,
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot,
        AzBone bone
    ) {
        if (keyframes.xKeyframes().isEmpty()) {
            return;
        }

        var initialSnapshot = bone.getInitialAzSnapshot();
        var x = getAnimationPointAtTick(keyframes.xKeyframes(), 0, true, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), 0, true, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), 0, true, Axis.Z);

        queue.addNextRotation(null, adjustedTick, transitionLength, snapshot, initialSnapshot, x, y, z);
    }

    private void transitionPosition(
        double adjustedTick,
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot
    ) {
        var x = getAnimationPointAtTick(keyframes.xKeyframes(), 0, false, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), 0, false, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), 0, false, Axis.Z);
        queue.addNextPosition(null, adjustedTick, transitionLength, snapshot, x, y, z);
    }

    private void transitionScale(
        double adjustedTick,
        AzKeyframeStack<AzKeyframe<IValue>> keyframes,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot
    ) {
        if (keyframes.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyframes.xKeyframes(), 0, false, Axis.X);
        var y = getAnimationPointAtTick(keyframes.yKeyframes(), 0, false, Axis.Y);
        var z = getAnimationPointAtTick(keyframes.zKeyframes(), 0, false, Axis.Z);

        queue.addNextScale(null, adjustedTick, transitionLength, snapshot, x, y, z);
    }
}

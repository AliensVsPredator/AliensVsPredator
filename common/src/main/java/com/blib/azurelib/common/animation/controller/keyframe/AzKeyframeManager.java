package com.blib.azurelib.common.animation.controller.keyframe;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.controller.AzBoneAnimationQueueCache;
import com.blib.azurelib.common.animation.controller.AzBoneSnapshotCache;

public class AzKeyframeManager<T> {

    private final AzKeyframeCallbackHandler<T> keyframeCallbackHandler;

    private final AzKeyframeExecutor<T> keyframeExecutor;

    private final AzKeyframeTransitioner<T> keyframeTransitioner;

    public AzKeyframeManager(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache,
        AzBoneSnapshotCache boneSnapshotCache,
        AzKeyframeCallbacks<T> keyframeCallbacks
    ) {
        this.keyframeCallbackHandler = new AzKeyframeCallbackHandler<>(animationController, keyframeCallbacks);
        this.keyframeExecutor = new AzKeyframeExecutor<>(animationController, boneAnimationQueueCache);
        this.keyframeTransitioner = new AzKeyframeTransitioner<>(
            animationController,
            boneAnimationQueueCache,
            boneSnapshotCache
        );
    }

    public AzKeyframeCallbackHandler<T> keyframeCallbackHandler() {
        return keyframeCallbackHandler;
    }

    public AzKeyframeExecutor<T> keyframeExecutor() {
        return keyframeExecutor;
    }

    public AzKeyframeTransitioner<T> keyframeTransitioner() {
        return keyframeTransitioner;
    }
}

package com.blib.internal.client.animation.controller.keyframe;

import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.api.client.animation.v1.keyframe.AzKeyframeCallbacks;
import com.blib.internal.client.animation.controller.AzBoneAnimationQueueCache;
import com.blib.internal.client.animation.controller.AzBoneSnapshotCache;

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

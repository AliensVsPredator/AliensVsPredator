package com.blib.internal.client.animation.track.keyframe;

import com.blib.api.client.animation.v1.keyframe.AzKeyframeCallbacks;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.internal.client.animation.track.AzBoneAnimationQueueCache;
import com.blib.internal.client.animation.track.AzBoneSnapshotCache;

public class AzKeyframeManager<T> {

    private final AzKeyframeCallbackHandler<T> keyframeCallbackHandler;

    private final AzKeyframeExecutor<T> keyframeExecutor;

    private final AzKeyframeTransitioner<T> keyframeTransitioner;

    public AzKeyframeManager(
        AzAnimationTrack<T> animationTrack,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache,
        AzBoneSnapshotCache boneSnapshotCache,
        AzKeyframeCallbacks<T> keyframeCallbacks
    ) {
        this.keyframeCallbackHandler = new AzKeyframeCallbackHandler<>(animationTrack, keyframeCallbacks);
        this.keyframeExecutor = new AzKeyframeExecutor<>(animationTrack, boneAnimationQueueCache);
        this.keyframeTransitioner = new AzKeyframeTransitioner<>(
            animationTrack,
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

package com.blib.azurelib.common.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import com.blib.azurelib.common.animation.AzBoneAnimationUpdateUtil;
import com.blib.azurelib.common.animation.cache.AzBoneCache;
import com.blib.azurelib.common.animation.controller.keyframe.AzBoneAnimationQueue;
import com.blib.azurelib.common.animation.easing.AzEasingType;

public class AzBoneAnimationQueueCache<T> {

    private final Map<String, AzBoneAnimationQueue> boneAnimationQueues;

    private final AzBoneCache boneCache;

    public AzBoneAnimationQueueCache(AzBoneCache boneCache) {
        this.boneAnimationQueues = new Object2ObjectOpenHashMap<>();
        this.boneCache = boneCache;
    }

    public void update(AzEasingType easingType) {
        var boneSnapshots = boneCache.getBoneSnapshotsByName();

        for (var boneAnimation : boneAnimationQueues.values()) {
            var bone = boneAnimation.bone();
            var snapshot = boneSnapshots.get(bone.getName());
            var initialSnapshot = bone.getInitialAzSnapshot();

            AzBoneAnimationUpdateUtil.updateRotations(boneAnimation, bone, easingType, initialSnapshot, snapshot);
            AzBoneAnimationUpdateUtil.updatePositions(boneAnimation, bone, easingType, snapshot);
            AzBoneAnimationUpdateUtil.updateScale(boneAnimation, bone, easingType, snapshot);
        }
    }

    public Collection<AzBoneAnimationQueue> values() {
        return boneAnimationQueues.values();
    }

    public @Nullable AzBoneAnimationQueue getOrNull(String boneName) {
        var bone = boneCache.getBakedModel().getBoneOrNull(boneName);

        if (bone == null) {
            return null;
        }

        return boneAnimationQueues.computeIfAbsent(boneName, $ -> new AzBoneAnimationQueue(bone));
    }

    public void clear() {
        boneAnimationQueues.clear();
    }
}

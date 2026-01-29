package com.blib.azurelib.common.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import com.blib.azurelib.common.animation.primitive.AzQueuedAnimation;
import com.blib.azurelib.common.model.AzBoneSnapshot;

public class AzBoneSnapshotCache {

    private final Map<String, AzBoneSnapshot> boneSnapshots;

    public AzBoneSnapshotCache() {
        this.boneSnapshots = new Object2ObjectOpenHashMap<>();
    }

    public void put(AzQueuedAnimation animation, Collection<AzBoneSnapshot> snapshots) {
        if (animation.animation().boneAnimations() == null) {
            return;
        }

        for (var snapshot : snapshots) {
            for (var boneAnimation : animation.animation().boneAnimations()) {
                if (boneAnimation.boneName().equals(snapshot.getBone().getName())) {
                    boneSnapshots.put(boneAnimation.boneName(), AzBoneSnapshot.copy(snapshot));
                    break;
                }
            }
        }
    }

    public @Nullable AzBoneSnapshot getOrNull(String name) {
        return boneSnapshots.get(name);
    }
}

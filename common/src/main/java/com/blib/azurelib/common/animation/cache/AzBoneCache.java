package com.blib.azurelib.common.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

import com.blib.azurelib.common.animation.AzAnimationContext;
import com.blib.azurelib.common.animation.AzCachedBoneUpdateUtil;
import com.blib.azurelib.common.model.AzBakedModel;
import com.blib.azurelib.common.model.AzBone;
import com.blib.azurelib.common.model.AzBoneSnapshot;

public class AzBoneCache {

    private AzBakedModel templateModel;

    private AzBakedModel bakedModel;

    private final Map<String, AzBoneSnapshot> boneSnapshotsByName;

    public AzBoneCache() {
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
        setBakedModel(AzBakedModel.getDefault());
    }

    public boolean setActiveModel(AzBakedModel model) {
        if (model == null) {
            this.templateModel = null;
            this.bakedModel = AzBakedModel.getDefault();
            boneSnapshotsByName.clear();
            return true;
        }

        if (this.templateModel == model) {
            return false;
        }

        this.templateModel = model;
        this.bakedModel = model.deepCopy();
        boneSnapshotsByName.clear();
        snapshot();

        return true;
    }

    public void update(AzAnimationContext<?> context) {
        var config = context.config();
        var timer = context.timer();
        var animTime = timer.getAnimTime();
        var boneSnapshots = getBoneSnapshotsByName();
        var resetTickLength = config.boneResetTime();

        // Updates the cached bone snapshots (only if they have changed).
        for (var bone : bakedModel.getBonesByName().values()) {
            AzCachedBoneUpdateUtil.updateCachedBoneRotation(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBonePosition(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBoneScale(bone, boneSnapshots, animTime, resetTickLength);
        }

        resetBoneTransformationMarkers();
    }

    private void resetBoneTransformationMarkers() {
        bakedModel.getBonesByName().values().forEach(AzBone::resetStateChanges);
    }

    private void snapshot() {
        boneSnapshotsByName.clear();

        for (var bone : bakedModel.getBonesByName().values()) {
            boneSnapshotsByName.put(bone.getName(), AzBoneSnapshot.copy(bone.getInitialAzSnapshot()));
        }
    }

    public void setBakedModel(AzBakedModel model) {
        this.bakedModel = (model != null) ? model : AzBakedModel.getDefault();
    }

    public AzBakedModel getBakedModel() {
        return bakedModel;
    }

    public Map<String, AzBoneSnapshot> getBoneSnapshotsByName() {
        return boneSnapshotsByName;
    }

    public boolean isEmpty() {
        return bakedModel.getBonesByName().isEmpty();
    }
}

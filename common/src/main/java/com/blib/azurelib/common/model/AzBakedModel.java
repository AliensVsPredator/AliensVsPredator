package com.blib.azurelib.common.model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a baked 3D model consisting of hierarchical bone structures. This class is immutable and provides
 * read-only access to bones by name or as a list of top-level bones. Bones are uniquely identified by their names.
 */
public class AzBakedModel {

    private static AzBakedModel defaultModel = new AzBakedModel(List.of());

    private final Map<String, com.blib.azurelib.common.model.AzBone> bonesByName;

    private final List<com.blib.azurelib.common.model.AzBone> topLevelBones;

    public AzBakedModel(List<com.blib.azurelib.common.model.AzBone> topLevelBones) {
        this.topLevelBones = Collections.unmodifiableList(topLevelBones);
        this.bonesByName = Collections.unmodifiableMap(mapBonesByName(topLevelBones));
    }

    private Map<String, com.blib.azurelib.common.model.AzBone> mapBonesByName(List<com.blib.azurelib.common.model.AzBone> bones) {
        var bonesByName = new HashMap<String, com.blib.azurelib.common.model.AzBone>();
        var nodesToMap = new ArrayDeque<>(bones);

        while (!nodesToMap.isEmpty()) {
            var currentBone = nodesToMap.poll();
            nodesToMap.addAll(currentBone.getChildBones());
            currentBone.saveInitialSnapshot();
            bonesByName.put(currentBone.getName(), currentBone);
        }

        return bonesByName;
    }

    public AzBakedModel deepCopy() {
        List<com.blib.azurelib.common.model.AzBone> copied = new ArrayList<>(this.topLevelBones.size());
        for (com.blib.azurelib.common.model.AzBone bone : this.topLevelBones) {
            copied.add(bone.deepCopy()); // each child deepCopy() calls saveInitialSnapshot()
        }
        return new AzBakedModel(copied); // this will rebuild bonesByName internally
    }

    public @Nullable com.blib.azurelib.common.model.AzBone getBoneOrNull(String name) {
        return bonesByName.get(name);
    }

    public Optional<com.blib.azurelib.common.model.AzBone> getBone(String name) {
        return Optional.ofNullable(getBoneOrNull(name));
    }

    public Map<String, com.blib.azurelib.common.model.AzBone> getBonesByName() {
        return bonesByName;
    }

    public List<AzBone> getTopLevelBones() {
        return topLevelBones;
    }

    public static AzBakedModel getDefault() {
        return defaultModel;
    }

    public static void setDefault(AzBakedModel model) {
        defaultModel = model != null ? model : new AzBakedModel(List.of());
    }
}

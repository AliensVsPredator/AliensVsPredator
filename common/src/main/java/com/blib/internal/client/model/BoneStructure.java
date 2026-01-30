package com.blib.internal.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public record BoneStructure(
    Bone self,
    Map<String, BoneStructure> children
) {

    public BoneStructure(Bone self) {
        this(self, new Object2ObjectOpenHashMap<>());
    }
}

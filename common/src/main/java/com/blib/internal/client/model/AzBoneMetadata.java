package com.blib.internal.client.model;

import org.jetbrains.annotations.Nullable;

import com.blib.api.client.model.v1.AzBone;

public record AzBoneMetadata(
    @Nullable Boolean dontRender,
    @Nullable Double inflate,
    Boolean mirror,
    String name,
    @Nullable AzBone parent,
    @Nullable Boolean reset
) {

    public AzBoneMetadata(Bone bone, AzBone parent) {
        this(bone.neverRender(), bone.inflate(), bone.mirror(), bone.name(), parent, bone.reset());
    }
}

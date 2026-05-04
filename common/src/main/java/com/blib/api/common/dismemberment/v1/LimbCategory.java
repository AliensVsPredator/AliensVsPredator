package com.blib.api.common.dismemberment.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record LimbCategory(ResourceLocation id) {

    public LimbCategory {
        Objects.requireNonNull(id, "LimbCategory id must not be null");
    }
}

package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record LimbCategory(ResourceLocation id) {

    public LimbCategory {
        Objects.requireNonNull(id, "LimbCategory id must not be null");
    }

    public static final Codec<LimbCategory> CODEC = ResourceLocation.CODEC.xmap(LimbCategory::new, LimbCategory::id);
}

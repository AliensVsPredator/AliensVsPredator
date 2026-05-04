package com.blib.api.common.dismemberment.v1;

import net.minecraft.resources.ResourceLocation;

public final class LimbCategories {

    public static final LimbCategory HEAD = of("blib", "head");

    public static final LimbCategory TAIL = of("blib", "tail");

    public static final LimbCategory ARM = of("blib", "arm");

    public static final LimbCategory LEG = of("blib", "leg");

    public static LimbCategory of(String namespace, String path) {
        return new LimbCategory(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static LimbCategory of(ResourceLocation id) {
        return new LimbCategory(id);
    }

    private LimbCategories() {}
}

package com.compatibility;

import net.minecraft.resources.ResourceLocation;

public class CommonConstants {

    public static final String MOD_ID = "c";

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}

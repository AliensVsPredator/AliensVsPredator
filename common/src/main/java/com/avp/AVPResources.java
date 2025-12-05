package com.avp;

import net.minecraft.resources.ResourceLocation;

public class AVPResources {

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(AVP.MOD_ID, path);
    }
}

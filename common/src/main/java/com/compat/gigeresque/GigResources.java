package com.compat.gigeresque;

import net.minecraft.resources.ResourceLocation;

public class GigResources {

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(Gig.MOD_ID, path);
    }
}

package org.avp.common;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Boston Vanseghi
 */
public class AVPResources {

    public static ResourceLocation location(String location) {
        return new ResourceLocation(AVPConstants.MOD_ID, location);
    }

    public static ResourceLocation entityAnimationLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("animations/entity/%s.animation.json", entityName));
    }

    public static ResourceLocation entityGeoLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("geo/entity/%s.geo.json", entityName));
    }

    public static ResourceLocation entityTextureLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("textures/entity/%s.png", entityName));
    }

    public static ResourceLocation itemAnimationLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("animations/item/%s.animation.json", entityName));
    }

    public static ResourceLocation itemGeoLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("geo/item/%s.geo.json", entityName));
    }

    public static ResourceLocation itemTextureLocation(String entityName) {
        return new ResourceLocation(AVPConstants.MOD_ID, String.format("textures/item/%s.png", entityName));
    }

    private AVPResources() {
        throw new UnsupportedOperationException();
    }
}

package com.avp;

import net.minecraft.resources.ResourceLocation;

public class AVPResources {

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(AVP.MOD_ID, path);
    }

    public static ResourceLocation armorAnimationLocation(String name) {
        return location("animations/armor/" + name + ".animation.json");
    }

    public static ResourceLocation armorGeoModelLocation(String name) {
        return location("geo/armor/" + name + ".geo.json");
    }

    public static ResourceLocation armorTextureLocation(String name) {
        return location("textures/armor/" + name + ".png");
    }

    public static ResourceLocation entityAnimationLocation(String name) {
        return location("animations/entity/" + name + ".animation.json");
    }

    public static ResourceLocation entityGeoModelLocation(String name) {
        return location("geo/entity/" + name + ".geo.json");
    }

    public static ResourceLocation entityTextureLocation(String name) {
        return location("textures/entity/" + name + ".png");
    }

    public static ResourceLocation itemAnimationLocation(String name) {
        return location("animations/item/" + name + ".animation.json");
    }

    public static ResourceLocation itemGeoModelLocation(String name) {
        return location("geo/item/" + name + ".geo.json");
    }

    public static ResourceLocation itemTextureLocation(String name) {
        return location("textures/item/" + name + ".png");
    }

    public static ResourceLocation blockAnimationLocation(String name) {
        return location("animations/block/" + name + ".animation.json");
    }

    public static ResourceLocation blockGeoModelLocation(String name) {
        return location("geo/block/" + name + ".geo.json");
    }

    public static ResourceLocation blockTextureLocation(String name) {
        return location("textures/block/" + name + ".png");
    }
}

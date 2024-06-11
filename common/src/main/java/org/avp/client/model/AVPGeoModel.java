package org.avp.client.model;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animatable.GeoAnimatable;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import org.avp.common.AVPResources;

public class AVPGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    private static final Map<String, ResourceLocation> ANIMATION_RESOURCES = new HashMap<>();

    private static final Map<String, ResourceLocation> MODEL_RESOURCES = new HashMap<>();

    private static final Map<String, ResourceLocation> TEXTURE_RESOURCES = new HashMap<>();

    private final String registryName;

    public AVPGeoModel(String registryName, GeoModelType geoModelType) {
        super();
        this.registryName = registryName;
        switch (geoModelType) {
            case ITEM -> {
                ANIMATION_RESOURCES.computeIfAbsent(registryName, AVPResources::itemAnimationLocation);
                MODEL_RESOURCES.computeIfAbsent(registryName, AVPResources::itemGeoLocation);
                TEXTURE_RESOURCES.computeIfAbsent(registryName, AVPResources::itemTextureLocation);
            }
            case ENTITY -> {
                ANIMATION_RESOURCES.computeIfAbsent(registryName, AVPResources::entityAnimationLocation);
                MODEL_RESOURCES.computeIfAbsent(registryName, AVPResources::entityGeoLocation);
                TEXTURE_RESOURCES.computeIfAbsent(registryName, AVPResources::entityTextureLocation);
            }
        }
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ANIMATION_RESOURCES.get(registryName);
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return MODEL_RESOURCES.get(registryName);
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return TEXTURE_RESOURCES.get(registryName);
    }
}

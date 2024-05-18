package org.avp.client.model.entity.living;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.core.animatable.GeoAnimatable;
import net.minecraft.resources.ResourceLocation;
import org.avp.common.AVPResources;

import java.util.HashMap;
import java.util.Map;

public abstract class AVPGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    private static final Map<String, ResourceLocation> ANIMATION_RESOURCES = new HashMap<>();

    private static final Map<String, ResourceLocation> MODEL_RESOURCES = new HashMap<>();

    private static final Map<String, ResourceLocation> TEXTURE_RESOURCES = new HashMap<>();

    private final String registryName;

    protected AVPGeoModel(String registryName) {
        super();
        this.registryName = registryName;
        ANIMATION_RESOURCES.computeIfAbsent(registryName, AVPResources::entityAnimationLocation);
        MODEL_RESOURCES.computeIfAbsent(registryName, AVPResources::entityGeoLocation);
        TEXTURE_RESOURCES.computeIfAbsent(registryName, AVPResources::entityTextureLocation);
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

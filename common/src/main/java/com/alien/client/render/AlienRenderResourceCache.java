package com.alien.client.render;

import com.alien.common.model.alien.variant.AlienVariant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import com.avp.AVPResources;

public class AlienRenderResourceCache {

    private static final int ROYAL_FLAG = 1;

    private final String baseName;

    private final Function<ResourceLocation, RenderType> renderTypeFunction;

    private final ResourceLocation[] modelLocations;

    private final ResourceLocation[] textureLocations;

    private final RenderType[] renderTypes;

    public AlienRenderResourceCache(String baseName, Function<ResourceLocation, RenderType> renderTypeFunction) {
        this.baseName = baseName;
        this.renderTypeFunction = renderTypeFunction;

        var size = AlienVariant.VALUES.length * 2;

        this.modelLocations = new ResourceLocation[size];
        this.textureLocations = new ResourceLocation[size];
        this.renderTypes = new RenderType[size];
    }

    public ResourceLocation getOrCreateModelLocationForVariant(AlienVariant variant) {
        return getOrCreateModelLocationForVariant(variant, false);
    }

    public ResourceLocation getOrCreateModelLocationForVariant(AlienVariant variant, boolean isRoyal) {
        int index = getIndex(variant, isRoyal);
        var modelLocation = modelLocations[index];

        if (modelLocation == null) {
            modelLocation = AVPResources.entityGeoModelLocation(getResourceName(variant, isRoyal));
            modelLocations[index] = modelLocation;
        }

        return modelLocation;
    }

    public ResourceLocation getOrCreateTextureLocationForVariant(AlienVariant variant) {
        return getOrCreateTextureLocationForVariant(variant, false);
    }

    public ResourceLocation getOrCreateTextureLocationForVariant(AlienVariant variant, boolean isRoyal) {
        int index = getIndex(variant, isRoyal);
        var textureLocation = textureLocations[index];

        if (textureLocation == null) {
            textureLocation = AVPResources.entityTextureLocation(getResourceName(variant, isRoyal));
            textureLocations[index] = textureLocation;
        }

        return textureLocation;
    }

    public RenderType getOrCreateRenderTypeForVariant(AlienVariant variant) {
        return getOrCreateRenderTypeForVariant(variant, false);
    }

    public RenderType getOrCreateRenderTypeForVariant(AlienVariant variant, boolean isRoyal) {
        int index = getIndex(variant, isRoyal);
        var renderType = renderTypes[index];

        if (renderType == null) {
            var texLoc = getOrCreateTextureLocationForVariant(variant, isRoyal);
            renderType = renderTypeFunction.apply(texLoc);
            renderTypes[index] = renderType;
        }

        return renderType;
    }

    private String getResourceName(AlienVariant alienVariant, boolean isRoyal) {
        return getPrefixForRoyalResourceLocation(isRoyal) + getPrefixForVariantResourceLocation(alienVariant) + baseName;
    }

    public String getPrefixForRoyalResourceLocation(boolean isRoyal) {
        return isRoyal ? "royal_" : "";
    }

    public String getPrefixForVariantResourceLocation(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> "";
            case NETHER -> "nether_";
            case ABERRANT -> "aberrant_";
            case IRRADIATED -> "irradiated_";
        };
    }

    private int getIndex(AlienVariant variant, boolean isRoyal) {
        return (variant.ordinal() << 1) | (isRoyal ? ROYAL_FLAG : 0);
    }
}

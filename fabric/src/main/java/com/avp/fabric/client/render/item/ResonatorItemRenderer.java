package com.avp.fabric.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class ResonatorItemRenderer extends AzItemRenderer {

    public static final String NAME = "resonator";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public ResonatorItemRenderer() {
        super(AzItemRendererConfig.builder(GEO, TEX).useNewOffset(true).build());
    }
}

package com.human.client.render.item;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class ResonatorItemRenderer extends AzItemRenderer {

    public static final String NAME = "resonator";

    private static final ResourceLocation MODEL = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.blockTextureLocation(NAME);

    public ResonatorItemRenderer() {
        super(
            AzItemRendererConfig.builder(MODEL, TEXTURE)
                .useNewOffset(true)
                .build()
        );
    }
}

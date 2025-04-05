package com.avp.client.render.block;

import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.block.entity.TripMineBE;

public class TripMineRenderer extends AzBlockEntityRenderer<TripMineBE> {

    public static final String NAME = "trip_mine";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public TripMineRenderer() {
        super(
            AzBlockEntityRendererConfig.<TripMineBE>builder(GEO, TEX)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build()
        );
    }
}

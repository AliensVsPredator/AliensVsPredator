package com.predator.client.render.block;

import com.predator.common.gameplay.block.entity.TripMineBlockEntity;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class TripMineRenderer extends AzBlockEntityRenderer<TripMineBlockEntity> {

    public static final String NAME = "trip_mine";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public TripMineRenderer() {
        super(
            AzBlockEntityRendererConfig.<TripMineBlockEntity>builder(GEO, TEX)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build()
        );
    }
}

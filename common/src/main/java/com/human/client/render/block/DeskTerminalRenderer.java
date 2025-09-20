package com.human.client.render.block;

import com.human.common.gameplay.block.entity.power.impl.DeskTerminalBlockEntity;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class DeskTerminalRenderer extends AzBlockEntityRenderer<DeskTerminalBlockEntity> {

    public static final String NAME = "desk_terminal";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public DeskTerminalRenderer() {
        super(
            AzBlockEntityRendererConfig.<DeskTerminalBlockEntity>builder(GEO, TEX)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build()
        );
    }
}

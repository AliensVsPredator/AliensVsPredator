package com.human.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class DeskTerminalItemRenderer extends AzItemRenderer {

    public static final String NAME = "desk_terminal";

    private static final ResourceLocation MODEL = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.blockTextureLocation(NAME);

    public DeskTerminalItemRenderer() {
        super(
            AzItemRendererConfig.builder(MODEL, TEXTURE)
                .useNewOffset(true)
                .build()
        );
    }
}

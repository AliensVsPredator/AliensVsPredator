package com.avp.client.render.item;

import com.avp.AVPResources;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;

public class DeskTerminalItemRenderer extends AzItemRenderer {

    public static final String NAME = "desk_terminal";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public DeskTerminalItemRenderer() {
        super(AzItemRendererConfig.builder(GEO, TEX).useNewOffset(true).build());
    }
}

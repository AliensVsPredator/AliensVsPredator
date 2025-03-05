package com.avp.core.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.core.AVPResources;

public class SimpleItemRenderer extends AzItemRenderer {

    public SimpleItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .useNewOffset(true)
                .build()
        );
    }
}

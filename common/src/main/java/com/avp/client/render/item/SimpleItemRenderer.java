package com.avp.client.render.item;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;

import com.avp.AVPResources;

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

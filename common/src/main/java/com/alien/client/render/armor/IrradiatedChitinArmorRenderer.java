package com.alien.client.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class IrradiatedChitinArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "chitin";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation("irradiated_" + NAME);

    public IrradiatedChitinArmorRenderer() {
        super(
            AzArmorRendererConfig.builder(MODEL, TEXTURE)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build()
        );
    }
}

package com.predator.client.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class JunglePredatorArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "jungle_predator";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation(NAME);

    public JunglePredatorArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

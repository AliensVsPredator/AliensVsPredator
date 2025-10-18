package com.human.client.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class WYEliteArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "wy_elite";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation(NAME);

    public WYEliteArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

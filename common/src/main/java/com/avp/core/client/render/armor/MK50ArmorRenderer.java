package com.avp.core.client.render.armor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.core.AVPResources;

public class MK50ArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "mk50";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation(NAME);

    public MK50ArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

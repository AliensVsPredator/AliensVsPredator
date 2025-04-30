package com.avp.fabric.client.render.armor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class PlatedIrradiatedChitinArmorRenderer extends AzArmorRenderer {

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation("plated_chitin");

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation("plated_irradiated_chitin");

    public PlatedIrradiatedChitinArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

package com.avp.fabric.client.render.armor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.fabric.AVPResources;

public class PlatedAberrantChitinArmorRenderer extends AzArmorRenderer {

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation("plated_chitin");

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation("plated_aberrant_chitin");

    public PlatedAberrantChitinArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

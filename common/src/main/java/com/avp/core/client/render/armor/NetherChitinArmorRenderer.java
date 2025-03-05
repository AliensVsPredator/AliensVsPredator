package com.avp.core.client.render.armor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.core.AVPResources;

public class NetherChitinArmorRenderer extends AzArmorRenderer {

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation("chitin");

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation("nether_chitin");

    public NetherChitinArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}

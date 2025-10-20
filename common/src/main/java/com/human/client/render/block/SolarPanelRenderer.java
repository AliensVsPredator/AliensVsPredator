package com.human.client.render.block;

import com.human.client.animation.block.SolarPanelAnimator;
import com.human.common.gameplay.block.entity.power.impl.SolarPanelBlockEntity;
import mod.azure.azurelib.common.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.common.render.block.AzBlockEntityRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class SolarPanelRenderer extends AzBlockEntityRenderer<SolarPanelBlockEntity> {

    public static final String NAME = "solar_panel";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.blockTextureLocation(NAME);

    public SolarPanelRenderer() {
        super(
            AzBlockEntityRendererConfig.<SolarPanelBlockEntity>builder(MODEL_LOCATION, TEXTURE_LOCATION)
                .setAnimatorProvider(SolarPanelAnimator::new)
                .build()
        );
    }
}

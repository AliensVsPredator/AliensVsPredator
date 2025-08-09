package com.human.client.render.block;

import com.avp.AVPResources;
import com.human.client.animation.block.SolarPanelAnimator;
import com.human.common.gameplay.block.entity.power.impl.SolarPanelBlockEntity;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import net.minecraft.resources.ResourceLocation;

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

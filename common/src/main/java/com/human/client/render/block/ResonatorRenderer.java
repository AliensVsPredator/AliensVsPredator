package com.human.client.render.block;

import com.human.client.animation.block.ResonatorAnimator;
import com.human.common.gameplay.block.entity.power.impl.ResonatorBlockEntity;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class ResonatorRenderer extends AzBlockEntityRenderer<ResonatorBlockEntity> {

    public static final String NAME = "resonator";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.blockTextureLocation(NAME);

    public ResonatorRenderer() {
        super(
            AzBlockEntityRendererConfig.<ResonatorBlockEntity>builder(MODEL_LOCATION, TEXTURE_LOCATION)
                .setAnimatorProvider(ResonatorAnimator::new)
                .build()
        );
    }
}

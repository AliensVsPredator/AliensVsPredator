package com.human.client.render.block;

import com.human.client.animation.block.ResonatorAnimator;
import com.human.common.gameplay.block.entity.ResonatorBlockEntity;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class ResonatorRenderer extends AzBlockEntityRenderer<ResonatorBlockEntity> {

    public static final String NAME = "resonator";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public ResonatorRenderer() {
        super(
            AzBlockEntityRendererConfig.<ResonatorBlockEntity>builder(GEO, TEX)
                .setAnimatorProvider(ResonatorAnimator::new)
                .build()
        );
    }
}

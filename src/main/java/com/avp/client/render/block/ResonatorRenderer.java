package com.avp.client.render.block;

import com.avp.AVPResources;
import com.avp.client.animation.blocks.ResonatorAnimator;
import com.avp.common.block.entity.ResonatorBE;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import net.minecraft.resources.ResourceLocation;

public class ResonatorRenderer extends AzBlockEntityRenderer<ResonatorBE> {

    public static final String NAME = "resonator";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public ResonatorRenderer() {
        super(AzBlockEntityRendererConfig.<ResonatorBE>builder(GEO, TEX)
                .setAnimatorProvider(ResonatorAnimator::new)
                .build());
    }
}

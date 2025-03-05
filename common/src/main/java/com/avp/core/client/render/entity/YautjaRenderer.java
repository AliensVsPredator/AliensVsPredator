package com.avp.core.client.render.entity;

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.core.AVPResources;
import com.avp.core.client.animation.YautjaAnimator;
import com.avp.core.common.entity.living.yautja.Yautja;

public class YautjaRenderer extends AzEntityRenderer<Yautja> {

    private static final String NAME = "yautja";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public YautjaRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<Yautja>builder(MODEL, TEXTURE).setAnimatorProvider(YautjaAnimator::new).build(), context);
        this.shadowRadius = 0.5F;
    }
}

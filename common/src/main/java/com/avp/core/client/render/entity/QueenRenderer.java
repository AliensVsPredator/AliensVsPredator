package com.avp.core.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.living.alien.xenomorph.queen.Queen;

public class QueenRenderer extends AzEntityRenderer<Queen> {

    private static final String NAME = "queen";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public QueenRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<Queen>builder(MODEL, TEXTURE).build(), context);
        this.shadowRadius = 1F;
    }

    @Override
    public void render(
        @NotNull Queen entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        entity.runPassiveAnimations();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

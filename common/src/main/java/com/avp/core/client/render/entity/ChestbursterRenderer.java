package com.avp.core.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.client.animation.ChestbursterAnimator;
import com.avp.core.common.entity.living.alien.chestburster.Chestburster;

public class ChestbursterRenderer extends AzEntityRenderer<Chestburster> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "chestburster";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public ChestbursterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, ChestbursterRenderer::textureLocation)
                .setAnimatorProvider(ChestbursterAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = DEFAULT_SHADOW_SIZE;
    }

    @Override
    public void render(
        @NotNull Chestburster entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        var scale = 0.4F;
        shadowRadius = DEFAULT_SHADOW_SIZE * scale;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        entity.runPassiveAnimations();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    public static ResourceLocation textureLocation(Chestburster chestburster) {
        if (chestburster.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        return chestburster.isAberrant() ? ABERRANT_TEXTURE : TEXTURE;
    }
}

package com.alien.client.render.entity;

import com.alien.client.animation.entity.PredalienAdolescentAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.predalien_adolescent.PredalienAdolescent;
import com.alien.common.model.alien.variant.AlienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PredalienAdolescentRenderer extends AzEntityRenderer<PredalienAdolescent> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "predalien_adolescent";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public PredalienAdolescentRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(PredalienAdolescentRenderer::modelLocation, PredalienAdolescentRenderer::textureLocation)
                .setRenderType(PredalienAdolescentRenderer::renderType)
                .setAnimatorProvider(PredalienAdolescentAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = DEFAULT_SHADOW_SIZE;
    }

    @Override
    public void render(
        @NotNull PredalienAdolescent entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        var scale = 0.7F;
        shadowRadius = DEFAULT_SHADOW_SIZE * scale;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static ResourceLocation modelLocation(PredalienAdolescent predalienAdolescent) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(PredalienAdolescent predalienAdolescent) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(predalienAdolescent.getVariant());
    }

    private static ResourceLocation textureLocation(PredalienAdolescent predalienAdolescent) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(predalienAdolescent.getVariant());
    }
}

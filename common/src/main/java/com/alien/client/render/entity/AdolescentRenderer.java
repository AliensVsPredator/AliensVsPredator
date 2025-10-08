package com.alien.client.render.entity;

import com.alien.client.animation.entity.AdolescentAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.adolescent.Adolescent;
import com.alien.common.model.alien.variant.AlienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AdolescentRenderer extends AzEntityRenderer<Adolescent> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "adolescent";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public AdolescentRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(AdolescentRenderer::modelLocation, AdolescentRenderer::textureLocation)
                .setRenderType(AdolescentRenderer::renderType)
                .setAnimatorProvider(AdolescentAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = DEFAULT_SHADOW_SIZE;
    }

    @Override
    public void render(
        @NotNull Adolescent entity,
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

    private static ResourceLocation modelLocation(Adolescent adolescent) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL, adolescent.isRoyal());
    }

    private static RenderType renderType(Adolescent adolescent) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(adolescent.getVariant(), adolescent.isRoyal());
    }

    private static ResourceLocation textureLocation(Adolescent adolescent) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(adolescent.getVariant(), adolescent.isRoyal());
    }
}

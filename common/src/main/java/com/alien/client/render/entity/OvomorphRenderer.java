package com.alien.client.render.entity;

import com.alien.client.animation.entity.OvomorphAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.variant.AlienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OvomorphRenderer extends AzEntityRenderer<Ovomorph> {

    private static final String NAME = "ovomorph";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    private static final AlienRenderResourceCache TRANSPARENT_RESOURCE_CACHE = new AlienRenderResourceCache(
        NAME,
        RenderType::entityTranslucent
    );

    public OvomorphRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(OvomorphRenderer::modelLocation, OvomorphRenderer::textureLocation)
                .setRenderType(OvomorphRenderer::getEggRenderType)
                .setAnimatorProvider(OvomorphAnimator::new)
                .setShadowRadius(0.4F)
                .build(),
            context
        );
    }

    @Override
    public void render(
        @NotNull Ovomorph entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        var scale = 1.35F;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static RenderType getEggRenderType(Ovomorph ovomorph) {
        if (
            ovomorph.getHatchManager().isHatching()
                || ovomorph.getHatchManager().isHatched()
        ) {
            return TRANSPARENT_RESOURCE_CACHE.getOrCreateRenderTypeForVariant(ovomorph.getVariant());
        }

        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(ovomorph.getVariant());
    }

    private static ResourceLocation modelLocation(Ovomorph ovomorph) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL, ovomorph.isRoyal());
    }

    private static RenderType renderType(Ovomorph ovomorph) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(ovomorph.getVariant(), ovomorph.isRoyal());
    }

    private static ResourceLocation textureLocation(Ovomorph ovomorph) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(ovomorph.getVariant(), ovomorph.isRoyal());
    }
}

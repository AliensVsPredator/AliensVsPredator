package com.alien.client.render.entity;

import com.alien.client.animation.entity.ChestbursterAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.chestburster.Chestburster;
import com.alien.common.model.alien.variant.AlienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ChestbursterRenderer extends AzEntityRenderer<Chestburster> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "chestburster";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public ChestbursterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(ChestbursterRenderer::modelLocation, ChestbursterRenderer::textureLocation)
                .setRenderType(ChestbursterRenderer::renderType)
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
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static ResourceLocation modelLocation(Chestburster chestburster) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL, chestburster.isRoyal());
    }

    private static RenderType renderType(Chestburster chestburster) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(chestburster.getVariant(), chestburster.isRoyal());
    }

    private static ResourceLocation textureLocation(Chestburster chestburster) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(chestburster.getVariant(), chestburster.isRoyal());
    }
}

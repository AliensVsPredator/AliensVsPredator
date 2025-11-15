package com.alien.client.render.entity;

import com.alien.client.animation.entity.PredalienChestbursterAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.predalien_chestburster.PredalienChestburster;
import com.alien.common.model.alien.variant.AlienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PredalienChestbursterRenderer extends AzEntityRenderer<PredalienChestburster> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "predalien_chestburster";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public PredalienChestbursterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(PredalienChestbursterRenderer::modelLocation, PredalienChestbursterRenderer::textureLocation)
                .setRenderType(PredalienChestbursterRenderer::renderType)
                .setAnimatorProvider(PredalienChestbursterAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = DEFAULT_SHADOW_SIZE;
    }

    @Override
    public void render(
        @NotNull PredalienChestburster entity,
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

    private static ResourceLocation modelLocation(PredalienChestburster predalienChestburster) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(PredalienChestburster predalienChestburster) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(predalienChestburster.getVariant());
    }

    private static ResourceLocation textureLocation(PredalienChestburster predalienChestburster) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(predalienChestburster.getVariant());
    }
}

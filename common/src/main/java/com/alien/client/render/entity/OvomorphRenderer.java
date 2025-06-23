package com.alien.client.render.entity;

import com.alien.client.animation.entity.OvomorphAnimator;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class OvomorphRenderer extends AzEntityRenderer<Ovomorph> {

    private static final String NAME = "ovomorph";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation ROYAL_MODEL = AVPResources.entityGeoModelLocation("royal_" + NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    private static final ResourceLocation ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_" + NAME);

    private static final ResourceLocation ABERRANT_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_aberrant_" + NAME);

    private static final ResourceLocation NETHER_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_nether_" + NAME);

    public OvomorphRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(OvomorphRenderer::modelLocation, OvomorphRenderer::textureLocation)
                .setAnimatorProvider(OvomorphAnimator::new)
                .setRenderType(OvomorphRenderer::getEggRenderType)
                .build(),
            context
        );
        this.shadowRadius = 0.4F;
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
        var maxSpawnCount = entity.maxSpawnCount.get();
        var additiveScale = 0.35F * maxSpawnCount;
        var scale = 1.05F + Math.max(additiveScale, 0);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    public static ResourceLocation modelLocation(Ovomorph ovomorph) {
        if (ovomorph.isRoyal()) {
            return ROYAL_MODEL;
        }

        return MODEL;
    }

    // TODO: Add textures with transparency to so the ovomorph will be transparent when hatched.
    public static ResourceLocation textureLocation(Ovomorph ovomorph) {
        if (ovomorph.isRoyal()) {
            if (ovomorph.isNetherAfflicted()) {
                return NETHER_ROYAL_TEXTURE;
            }
            if (ovomorph.isAberrant()) {
                return ABERRANT_ROYAL_TEXTURE;
            }
            return ROYAL_TEXTURE;
        }

        if (!ovomorph.isRoyal()) {
            if (ovomorph.isNetherAfflicted()) {
                return NETHER_TEXTURE;
            }

            if (ovomorph.isAberrant()) {
                return ABERRANT_TEXTURE;
            }
        }

        return TEXTURE;
    }

    public static RenderType getEggRenderType(Ovomorph ovomorph) {
        if (
            ovomorph.getHatchManager().isHatching()
                || ovomorph.getHatchManager().isHatched()
        ) {
            return RenderType.entityTranslucent(textureLocation(ovomorph));
        }

        return RenderType.entityCutoutNoCull(textureLocation(ovomorph));
    }
}

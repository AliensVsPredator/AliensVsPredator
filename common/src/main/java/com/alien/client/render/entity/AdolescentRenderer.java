package com.alien.client.render.entity;

import com.alien.client.animation.entity.AdolescentAnimator;
import com.alien.common.gameplay.entity.living.alien.adolescent.Adolescent;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class AdolescentRenderer extends AzEntityRenderer<Adolescent> {

    private static final float DEFAULT_SHADOW_SIZE = 0.4F;

    private static final String NAME = "adolescent";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation ROYAL_MODEL = AVPResources.entityGeoModelLocation("royal_" + NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    private static final ResourceLocation ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_" + NAME);

    private static final ResourceLocation ABERRANT_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_aberrant_" + NAME);

    private static final ResourceLocation NETHER_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_nether_" + NAME);

    public AdolescentRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(AdolescentRenderer::modelLocation, AdolescentRenderer::textureLocation)
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

    public static ResourceLocation modelLocation(Adolescent adolescent) {
        if (adolescent.isRoyal()) {
            return ROYAL_MODEL;
        }

        return MODEL;
    }

    public static ResourceLocation textureLocation(Adolescent adolescent) {
        if (adolescent.isRoyal()) {
            if (adolescent.isNetherAfflicted()) {
                return NETHER_ROYAL_TEXTURE;
            }
            if (adolescent.isAberrant()) {
                return ABERRANT_ROYAL_TEXTURE;
            }
            return ROYAL_TEXTURE;
        }

        if (!adolescent.isRoyal()) {
            if (adolescent.isNetherAfflicted()) {
                return NETHER_TEXTURE;
            }

            if (adolescent.isAberrant()) {
                return ABERRANT_TEXTURE;
            }
        }

        return TEXTURE;
    }
}

package com.avp.fabric.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.fabric.client.animation.entity.OvamorphAnimator;
import com.avp.fabric.common.entity.living.alien.ovamorph.Ovamorph;

public class OvamorphRenderer extends AzEntityRenderer<Ovamorph> {

    private static final String NAME = "ovamorph";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation ROYAL_MODEL = AVPResources.entityGeoModelLocation("royal_" + NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    private static final ResourceLocation ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_" + NAME);

    private static final ResourceLocation ABERRANT_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_aberrant_" + NAME);

    private static final ResourceLocation NETHER_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_nether_" + NAME);

    public OvamorphRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(OvamorphRenderer::modelLocation, OvamorphRenderer::textureLocation)
                .setAnimatorProvider(OvamorphAnimator::new)
                .setRenderType(OvamorphRenderer::getEggRenderType)
                .build(),
            context
        );
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(
        @NotNull Ovamorph entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        var maxSpawnCount = entity.hatchManager().maximumSpawnCount();
        var additiveScale = 0.35F * maxSpawnCount;
        var scale = 1.05F + Math.max(additiveScale, 0);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    public static ResourceLocation modelLocation(Ovamorph ovamorph) {
        if (ovamorph.isRoyal()) {
            return ROYAL_MODEL;
        }

        return MODEL;
    }

    // TODO: Add textures with transparency to so the ovamorph will be transparent when hatched.
    public static ResourceLocation textureLocation(Ovamorph ovamorph) {
        if (ovamorph.isRoyal()) {
            if (ovamorph.isNetherAfflicted()) {
                return NETHER_ROYAL_TEXTURE;
            }
            if (ovamorph.isAberrant()) {
                return ABERRANT_ROYAL_TEXTURE;
            }
            return ROYAL_TEXTURE;
        }

        if (!ovamorph.isRoyal()) {
            if (ovamorph.isNetherAfflicted()) {
                return NETHER_TEXTURE;
            }

            if (ovamorph.isAberrant()) {
                return ABERRANT_TEXTURE;
            }
        }

        return TEXTURE;
    }

    public static RenderType getEggRenderType(Ovamorph ovamorph) {
        if (ovamorph.hatchManager().hatched()) {
            return RenderType.entityTranslucent(textureLocation(ovamorph));
        }

        return RenderType.entityCutoutNoCull(textureLocation(ovamorph));
    }
}

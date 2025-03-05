package com.avp.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.client.animation.OvamorphAnimator;
import com.avp.common.entity.living.alien.ovamorph.Ovamorph;

public class OvamorphRenderer extends AzEntityRenderer<Ovamorph> {

    private static final String NAME = "ovamorph";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public OvamorphRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, OvamorphRenderer::textureLocation)
                .setAnimatorProvider(OvamorphAnimator::new)
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

    public static ResourceLocation textureLocation(Ovamorph ovamorph) {
        if (ovamorph.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        return ovamorph.isAberrant() ? ABERRANT_TEXTURE : TEXTURE;
    }

    // TODO:
    // @Override
    // public RenderType getRenderType(Ovamorph animatable, ResourceLocation texture, @Nullable MultiBufferSource
    // bufferSource, float partialTick) {
    // return RenderType.entityTranslucent(texture);
    // }
}

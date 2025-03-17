package com.avp.client.render.entity;

import com.avp.client.render.layer.RadiationGlowLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.client.animation.PraetorianAnimator;
import com.avp.common.entity.living.alien.xenomorph.praetorian.Praetorian;

public class PraetorianRenderer extends AzEntityRenderer<Praetorian> {

    private static final String NAME = "praetorian";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public PraetorianRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, PraetorianRenderer::textureLocation)
                .setAnimatorProvider(PraetorianAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Praetorian drone) {
        if (drone.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (drone.isIrraiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (drone.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }

    @Override
    public void render(
        @NotNull Praetorian entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        entity.runPassiveAnimations();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

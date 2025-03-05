package com.avp.core.client.render.entity.parasite.facehugger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.render.AzLayerRenderer;
import mod.azure.azurelib.rewrite.render.AzModelRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererPipeline;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.client.animation.FacehuggerAnimator;
import com.avp.core.common.entity.living.alien.parasite.facehugger.Facehugger;

public class FacehuggerRenderer extends AzEntityRenderer<Facehugger> {

    private static final String NAME = "facehugger";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, FacehuggerRenderer::textureLocation)
                .setAnimatorProvider(FacehuggerAnimator::new)
                .build(),
            context
        );
        this.shadowRadius = 0.25F;
    }

    @Override
    public void render(
        @NotNull Facehugger entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        if (!entity.attachmentManager().isFertile() && entity.attachmentManager().getHost() == null) {
            poseStack.translate(0, entity.getBbHeight(), 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        }

        entity.runPassiveAnimations();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected AzEntityRendererPipeline<Facehugger> createPipeline(AzEntityRendererConfig<Facehugger> config) {
        return new AzEntityRendererPipeline<>(config, this) {

            @Override
            protected AzModelRenderer<Facehugger> createModelRenderer(AzLayerRenderer<Facehugger> layerRenderer) {
                return new FacehuggerModelRenderer(this, layerRenderer);
            }
        };
    }

    public static ResourceLocation textureLocation(Facehugger facehugger) {
        if (facehugger.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        return facehugger.isAberrant() ? ABERRANT_TEXTURE : TEXTURE;
    }
}

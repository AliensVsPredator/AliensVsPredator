package com.avp.client.render.entity.parasite.facehugger;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.rewrite.render.AzLayerRenderer;
import mod.azure.azurelib.rewrite.render.AzModelRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererPipeline;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.client.animation.entity.FacehuggerAnimator;
import com.avp.common.entity.living.alien.parasite.facehugger.Facehugger;

public class FacehuggerRenderer extends AzEntityRenderer<Facehugger> {

    private static final String NAME = "facehugger";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation ROYAL_MODEL = AVPResources.entityGeoModelLocation("royal_" + NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    private static final ResourceLocation ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_" + NAME);

    private static final ResourceLocation ABERRANT_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_aberrant_" + NAME);

    private static final ResourceLocation NETHER_ROYAL_TEXTURE = AVPResources.entityTextureLocation("royal_nether_" + NAME);

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(FacehuggerRenderer::modelLocation, FacehuggerRenderer::textureLocation)
                .setAnimatorProvider(FacehuggerAnimator::new)
                .setDeathMaxRotation(0F)
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
        runPassiveAnimations(entity);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public void runPassiveAnimations(Facehugger facehugger) {
        var attachmentManager = facehugger.getAttachmentManager();
        var dispatcher = facehugger.getAnimationDispatcher();

        if ((!attachmentManager.isFertile() && !attachmentManager.isAttachedToHost()) || facehugger.isDeadOrDying()) {
            dispatcher.infertile();
            return;
        }

        if (attachmentManager.isAttachedToHost() && facehugger.isAlive()) {
            dispatcher.hug();
            return;
        }

        var movementAnalyzer = facehugger.getMovementAnalyzer();
        var isMovingOnGround = movementAnalyzer.isMovingHorizontally() && facehugger.onGround();

        if (facehugger.isUnderWater()) {
            // TODO: swim
        } else if (isMovingOnGround) {
            dispatcher.run();
        } else {
            dispatcher.idle();
        }
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

    public static ResourceLocation modelLocation(Facehugger facehugger) {
        if (facehugger.isRoyal()) {
            return ROYAL_MODEL;
        }

        return MODEL;
    }

    public static ResourceLocation textureLocation(Facehugger facehugger) {
        if (facehugger.isRoyal()) {
            if (facehugger.isNetherAfflicted()) {
                return NETHER_ROYAL_TEXTURE;
            }
            if (facehugger.isAberrant()) {
                return ABERRANT_ROYAL_TEXTURE;
            }
            return ROYAL_TEXTURE;
        }

        if (!facehugger.isRoyal()) {
            if (facehugger.isNetherAfflicted()) {
                return NETHER_TEXTURE;
            }

            if (facehugger.isAberrant()) {
                return ABERRANT_TEXTURE;
            }
        }

        return TEXTURE;
    }
}

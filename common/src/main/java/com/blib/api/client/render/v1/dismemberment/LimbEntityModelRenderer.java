package com.blib.api.client.render.v1.dismemberment;

import com.mojang.math.Axis;

import java.util.UUID;

import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.entity.model.AzEntityModelRenderer;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;

/**
 * Walks only the bone subtree rooted at the limb's bone instead of all top-level bones, so the limb entity renders just
 * the detached piece.
 */
public class LimbEntityModelRenderer extends AzEntityModelRenderer<DismemberedLimbEntity> {

    public LimbEntityModelRenderer(
        AzEntityRendererPipeline<DismemberedLimbEntity> entityRendererPipeline,
        AzLayerRenderer<UUID, DismemberedLimbEntity> layerRenderer
    ) {
        super(entityRendererPipeline, layerRenderer);
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, DismemberedLimbEntity> context, boolean isReRender) {
        var animatable = context.animatable();
        var bakedModel = context.bakedModel();
        var rootBoneName = animatable.getRootBoneName();

        if (bakedModel == null || rootBoneName == null || rootBoneName.isEmpty()) {
            return;
        }

        var rootBone = bakedModel.getBoneOrNull(rootBoneName);

        if (rootBone == null) {
            return;
        }

        var poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - animatable.getYRot()));

        // Authored rotation around the limb's anchor. PoseStack composes right-to-left,
        // so applying Z then Y then X here matches the geo bone Z*Y*X convention once the
        // pivot translation below puts the bone anchor at the entity origin.
        var renderRotation = animatable.getLimbRenderRotation();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));

        // Bedrock cube vertices are stored in absolute model-space coordinates, so
        // when we render a single bone in isolation its geometry would draw at the
        // height it occupied on the original entity. Translating by the bone's
        // pivot puts the bone's anchor at the limb entity's origin; the
        // user-supplied render offset then nudges it into the hitbox.
        var renderOffset = animatable.getLimbRenderOffset();
        poseStack.translate(
            -rootBone.getPivotX() / 16f + renderOffset.x,
            -rootBone.getPivotY() / 16f + renderOffset.y,
            -rootBone.getPivotZ() / 16f + renderOffset.z
        );

        if (!isReRender) {
            var animator = entityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                handleAnimation(animator, animatable, context.partialTick());
            }
        }

        entityRendererPipeline.getModelRenderTranslations().set(poseStack.last().pose());

        if (context.vertexConsumer() != null) {
            entityRendererPipeline.updateAnimatedTextureFrame(animatable);
            renderRecursively(context, rootBone, isReRender);
            entityRendererPipeline.config().renderEntry(context);
        }

        poseStack.popPose();
    }
}

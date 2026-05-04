package com.blib.api.client.render.v1.dismemberment;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;

import java.util.UUID;

import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.entity.AzEntityRenderer;
import com.blib.api.client.render.v1.entity.model.AzEntityModelRenderer;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.internal.mixin.MixinEntityRenderDispatcher_Accessor;

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
        var rootBoneName = animatable.getRootBoneName();

        if (rootBoneName == null || rootBoneName.isEmpty()) {
            return;
        }

        // Pick the render path based on the source mob's renderer. AzEntityRenderer means BLib
        // geo bones; anything else (LivingEntityRenderer subclasses, etc.) means vanilla
        // ModelPart rendering. The ghost is always live as long as the limb is, so this lookup
        // never falls back unless the source NBT hasn't synced yet.
        var ghost = animatable.getOrCreateGhost();

        if (ghost != null) {
            var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            var sourceRenderer = ((MixinEntityRenderDispatcher_Accessor) dispatcher).blib$getRenderers()
                .get(ghost.getType());

            if (!(sourceRenderer instanceof AzEntityRenderer<?>)) {
                if (!isReRender && context.vertexConsumer() != null) {
                    VanillaLimbRenderer.render(
                        animatable,
                        context.poseStack(),
                        context.vertexConsumer(),
                        context.packedLight(),
                        context.packedOverlay()
                    );

                    // Re-run the source mob's armor layer at the limb's pose so equipped armor follows the limb (e.g.
                    // helmet on a severed head). Bone-only path is preserved above; armor pass uses the buffer source.
                    if (context.multiBufferSource() != null) {
                        LimbArmorRenderer.render(
                            animatable,
                            context.poseStack(),
                            context.multiBufferSource(),
                            context.packedLight()
                        );
                    }
                }
                return;
            }
        }

        var bakedModel = context.bakedModel();

        if (bakedModel == null) {
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

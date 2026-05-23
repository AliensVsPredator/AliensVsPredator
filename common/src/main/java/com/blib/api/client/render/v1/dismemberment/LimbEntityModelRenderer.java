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
        var visuals = animatable.resolveVisuals();

        if (visuals == null) {
            return;
        }

        var rootBoneName = visuals.rootBoneName();

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

                    // Re-run the source mob's armor + held-item layers at the limb's pose so armor and any held item
                    // follow the limb (e.g. helmet on a severed head, bow on a severed arm). Bone-only path is
                    // preserved above; these passes use the buffer source.
                    if (context.multiBufferSource() != null) {
                        LimbArmorRenderer.render(
                            animatable,
                            context.poseStack(),
                            context.multiBufferSource(),
                            context.packedLight()
                        );
                        LimbHeldItemRenderer.render(
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
        LimbRenderTransforms.applySourceTransform(poseStack, animatable);

        var selectedPose = visuals.poseOrDefault(animatable.getPoseId());
        var renderOffset = selectedPose.renderOffset();
        var renderRotation = selectedPose.renderRotation();
        var renderScale = selectedPose.renderScale();
        if (selectedPose.modelerTransform()) {
            var renderPivot = selectedPose.renderPivot();
            poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
            poseStack.translate(renderPivot.x, renderPivot.y, renderPivot.z);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));
            poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);
            poseStack.translate(
                -renderPivot.x - rootBone.getPivotX() / 16f,
                -renderPivot.y - rootBone.getPivotY() / 16f,
                -renderPivot.z - rootBone.getPivotZ() / 16f
            );
        } else {
            // Legacy limb visuals treat render_offset as a local post-scale translation. Keep that ordering for
            // existing JSON that has no render_pivot field.
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderRotation.z));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) renderRotation.y));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) renderRotation.x));
            poseStack.scale((float) renderScale.x, (float) renderScale.y, (float) renderScale.z);
            poseStack.translate(
                -rootBone.getPivotX() / 16f + renderOffset.x,
                -rootBone.getPivotY() / 16f + renderOffset.y,
                -rootBone.getPivotZ() / 16f + renderOffset.z
            );
        }

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

            // Companion bones live outside the root subtree but ride along with this limb (e.g. authored sibling
            // bones the modeler kept independent of the head bone). Render each at the same pose stack as the root.
            if (!visuals.companionBoneNames().isEmpty()) {
                for (var companionBoneName : visuals.companionBoneNames()) {
                    var companionBone = bakedModel.getBoneOrNull(companionBoneName);

                    if (companionBone != null) {
                        renderRecursively(context, companionBone, isReRender);
                    }
                }
            }

            entityRendererPipeline.config().renderEntry(context);
        }

        poseStack.popPose();
    }
}

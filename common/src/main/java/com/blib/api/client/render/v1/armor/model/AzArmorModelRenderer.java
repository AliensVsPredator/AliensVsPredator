package com.blib.api.client.render.v1.armor.model;

import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.UUID;

import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.armor.pipeline.AzArmorRendererPipeline;
import com.blib.internal.client.render.util.RenderUtil;

public class AzArmorModelRenderer extends AzModelRenderer<UUID, ItemStack> {

    protected final AzArmorRendererPipeline armorRendererPipeline;

    public AzArmorModelRenderer(
        AzArmorRendererPipeline armorRendererPipeline,
        AzLayerRenderer<UUID, ItemStack> layerRenderer
    ) {
        super(armorRendererPipeline, layerRenderer);
        this.armorRendererPipeline = armorRendererPipeline;
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, ItemStack> context, boolean isReRender) {
        var poseStack = context.poseStack();

        poseStack.pushPose();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender) {
            var animatable = context.animatable();
            var animator = armorRendererPipeline.renderer().animator();

            if (animator != null) {
                handleAnimation(animator, animatable, context.partialTick());
            }
        }

        armorRendererPipeline.setModelRenderTranslations(new Matrix4f(poseStack.last().pose()));

        super.render(context, isReRender);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(AzRendererPipelineContext<UUID, ItemStack> context, AzBone bone, boolean isReRender) {
        var poseStack = context.poseStack();
        // TODO: This is dangerous.
        var ctx = armorRendererPipeline.context();

        poseStack.pushPose();
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(
                poseState,
                armorRendererPipeline.getEntityRenderTranslations()
            );

            bone.setModelSpaceMatrix(
                RenderUtil.invertAndMultiplyMatrices(poseState, armorRendererPipeline.getModelRenderTranslations())
            );
            bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, new Vector3f()));
            bone.setWorldSpaceMatrix(
                RenderUtil.translateMatrix(new Matrix4f(localMatrix), ctx.currentEntity().position().toVector3f())
            );
        }

        context.setVertexConsumer(getOrRefreshRenderBuffer(isReRender, context, bone));

        super.renderRecursively(context, bone, isReRender);

        poseStack.popPose();
    }
}

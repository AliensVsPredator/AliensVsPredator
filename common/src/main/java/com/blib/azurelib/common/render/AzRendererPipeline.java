package com.blib.azurelib.common.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import com.blib.azurelib.common.model.AzBakedModel;

public abstract class AzRendererPipeline<K, T> implements AzPhasedRenderer<K, T> {

    protected final AzRendererConfig<K, T> config;

    private final AzRendererPipelineContext<K, T> context;

    private final AzLayerRenderer<K, T> layerRenderer;

    private final AzModelRenderer<K, T> modelRenderer;

    protected AzRendererPipeline(AzRendererConfig<K, T> config) {
        this.config = config;
        this.context = createContext(this);
        this.layerRenderer = createLayerRenderer(config);
        this.modelRenderer = createModelRenderer(layerRenderer);
    }

    protected abstract AzRendererPipelineContext<K, T> createContext(AzRendererPipeline<K, T> rendererPipeline);

    protected abstract AzModelRenderer<K, T> createModelRenderer(AzLayerRenderer<K, T> layerRenderer);

    protected abstract AzLayerRenderer<K, T> createLayerRenderer(AzRendererConfig<K, T> config);

    protected abstract void updateAnimatedTextureFrame(T animatable);

    public void render(
        PoseStack poseStack,
        AzBakedModel model,
        T animatable,
        MultiBufferSource bufferSource,
        @Nullable RenderType renderType,
        @Nullable VertexConsumer buffer,
        float yaw,
        float partialTick,
        int packedLight
    ) {
        renderType = context.getDefaultRenderType(
            animatable,
            config.textureLocation(context.currentEntity, animatable),
            bufferSource,
            partialTick,
            config.getRenderType(context.currentEntity, animatable),
            config.alpha(animatable)
        );
        context.populate(
            animatable,
            model,
            bufferSource,
            packedLight,
            partialTick,
            poseStack,
            renderType,
            buffer
        );

        poseStack.pushPose();

        preRender(context, false);

        layerRenderer.preApplyRenderLayers(context);
        modelRenderer.render(context, false);
        layerRenderer.applyRenderLayers(context);
        postRender(context, false);

        poseStack.popPose();

        renderFinal(context);
        doPostRenderCleanup(context);
    }

    public void reRender(AzRendererPipelineContext<K, T> context) {
        var poseStack = context.poseStack();

        poseStack.pushPose();

        preRender(context, true);
        modelRenderer.render(context, true);
        postRender(context, true);

        poseStack.popPose();
    }

    protected void renderFinal(AzRendererPipelineContext<K, T> context) {}

    protected void doPostRenderCleanup(AzRendererPipelineContext<K, T> context) {}

    protected void scaleModelForRender(
        AzRendererPipelineContext<K, T> context,
        float widthScale,
        float heightScale,
        boolean isReRender
    ) {
        if (!isReRender && (widthScale != 1 || heightScale != 1)) {
            var poseStack = context.poseStack();
            poseStack.scale(widthScale, heightScale, widthScale);
        }
    }

    public AzRendererConfig<K, T> config() {
        return config;
    }

    public AzRendererPipelineContext<K, T> context() {
        return context;
    }
}

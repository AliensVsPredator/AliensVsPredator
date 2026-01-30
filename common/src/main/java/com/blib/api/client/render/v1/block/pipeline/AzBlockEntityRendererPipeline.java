package com.blib.api.client.render.v1.block.pipeline;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;

import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.block.AzBlockEntityRenderer;
import com.blib.api.client.render.v1.block.AzBlockEntityRendererConfig;
import com.blib.api.client.texture.v1.AnimatableTexture;

public class AzBlockEntityRendererPipeline<T extends BlockEntity> extends AzRendererPipeline<Long, T> {

    private final AzBlockEntityRenderer<T> blockEntityRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzBlockEntityRendererPipeline(
        AzBlockEntityRendererConfig config,
        AzBlockEntityRenderer<T> blockEntityRenderer
    ) {
        super(config);
        this.blockEntityRenderer = blockEntityRenderer;
    }

    @Override
    protected AzBlockEntityRendererPipelineContext<T> createContext(AzRendererPipeline<Long, T> rendererPipeline) {
        return (AzBlockEntityRendererPipelineContext<T>) config.pipelineContext(this);
    }

    @Override
    protected AzModelRenderer<Long, T> createModelRenderer(AzLayerRenderer<Long, T> layerRenderer) {
        return config.modelRendererProvider(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<Long, T> createLayerRenderer(AzRendererConfig<Long, T> config) {
        return new AzLayerRenderer<>(config::renderLayers);
    }

    @Override
    public void updateAnimatedTextureFrame(T entity) {
        AnimatableTexture.setAndUpdate(config.textureLocation(context().currentEntity(), entity));
    }

    @Override
    public void preRender(AzRendererPipelineContext<Long, T> context, boolean isReRender) {
        var poseStack = context.poseStack();
        this.entityRenderTranslations.set(poseStack.last().pose());

        var scaleWidth = config.scaleWidth(context.animatable());
        var scaleHeight = config.scaleHeight(context.animatable());
        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);
        if (config.alpha(context.animatable()) < 1) {
            var alpha = (int) (config.alpha(context.animatable()) * 0xFF) << 24;
            var color = (context.renderColor() & 0xFFFFFF) | alpha;
            context.setRenderColor(color);
        }
        config.preRenderEntry(context);
    }

    @Override
    public void postRender(AzRendererPipelineContext<Long, T> context, boolean isReRender) {
        config.postRenderEntry(context);
        context.setTextureOverride(null);
    }

    public void setModelRenderTranslations(Matrix4f modelRenderTranslations) {
        this.modelRenderTranslations = modelRenderTranslations;
    }

    public Matrix4f getEntityRenderTranslations() {
        return entityRenderTranslations;
    }

    public Matrix4f getModelRenderTranslations() {
        return modelRenderTranslations;
    }

    public AzBlockEntityRenderer<T> getRenderer() {
        return blockEntityRenderer;
    }
}

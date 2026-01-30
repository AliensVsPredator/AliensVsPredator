package com.blib.api.client.render.v1.entity.pipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.joml.Matrix4f;

import java.util.UUID;

import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.entity.AzEntityRenderer;
import com.blib.api.client.render.v1.entity.AzEntityRendererConfig;
import com.blib.api.client.render.v1.entity.layer.AzEntityLayerRenderer;
import com.blib.internal.client.render.entity.AzEntityLeashRenderUtil;
import com.blib.internal.client.texture.AnimatableTexture;

public class AzEntityRendererPipeline<T extends Entity> extends AzRendererPipeline<UUID, T> {

    private final AzEntityRenderer<T> entityRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzEntityRendererPipeline(
        AzEntityRendererConfig<T> config,
        AzEntityRenderer<T> entityRenderer
    ) {
        super(config);
        this.entityRenderer = entityRenderer;
    }

    @Override
    protected AzRendererPipelineContext<UUID, T> createContext(AzRendererPipeline<UUID, T> rendererPipeline) {
        return config.pipelineContext(this);
    }

    @Override
    protected AzModelRenderer<UUID, T> createModelRenderer(AzLayerRenderer<UUID, T> layerRenderer) {
        return config.modelRendererProvider(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<UUID, T> createLayerRenderer(AzRendererConfig<UUID, T> config) {
        return new AzEntityLayerRenderer<>(config::renderLayers);
    }

    @Override
    public void updateAnimatedTextureFrame(T entity) {
        AnimatableTexture.setAndUpdate(config.textureLocation(context().currentEntity(), entity));
    }

    @Override
    public void preRender(AzRendererPipelineContext<UUID, T> context, boolean isReRender) {
        var poseStack = context.poseStack();
        this.entityRenderTranslations.set(poseStack.last().pose());

        var config = entityRenderer.config();
        var scaleWidth = config.scaleWidth(context.animatable());
        var scaleHeight = config.scaleHeight(context.animatable());

        scaleModelForRender(context, scaleWidth, scaleHeight, isReRender);
        if (config.alpha(context.animatable()) < 1 || context.animatable().isInvisible()) {
            var setAlpha = context.animatable().isInvisible()
                ? (context.animatable()
                    .isInvisibleTo(
                        Minecraft.getInstance().player
                    ) ? 0 : 0.38)
                : config.alpha(context.animatable());
            var alpha = (int) (setAlpha * 0xFF) << 24;
            var color = (context.renderColor() & 0xFFFFFF) | alpha;
            context.setRenderColor(color);
        }
        config.preRenderEntry(context);
    }

    @Override
    public void postRender(AzRendererPipelineContext<UUID, T> context, boolean isReRender) {
        config.postRenderEntry(context);
        context.setTextureOverride(null);
    }

    @Override
    public void renderFinal(AzRendererPipelineContext<UUID, T> context) {
        var bufferSource = context.multiBufferSource();
        var entity = context.animatable();
        var packedLight = context.packedLight();
        var partialTick = context.partialTick();
        var poseStack = context.poseStack();

        entityRenderer.superRender(entity, 0, partialTick, poseStack, bufferSource, packedLight);

        if (!(entity instanceof Mob mob)) {
            return;
        }

        var leashHolder = mob.getLeashHolder();

        if (leashHolder == null) {
            return;
        }

        AzEntityLeashRenderUtil.renderLeash(entityRenderer, mob, partialTick, poseStack, bufferSource, leashHolder);
    }

    @Override
    protected void doPostRenderCleanup(AzRendererPipelineContext<UUID, T> context) {
        context.setCurrentEntity(null);
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

    public AzEntityRenderer<T> getRenderer() {
        return entityRenderer;
    }
}

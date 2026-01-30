package com.blib.api.client.render.v1.item.pipeline;

import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.UUID;
import java.util.stream.Stream;

import com.blib.api.client.render.v1.item.AzItemRenderer;
import com.blib.api.client.render.v1.item.AzItemRendererConfig;
import com.blib.api.client.render.v1.item.model.AzItemModelRenderer;
import com.blib.internal.client.render.AzLayerRenderer;
import com.blib.internal.client.render.AzRendererConfig;
import com.blib.internal.client.render.AzRendererPipeline;
import com.blib.internal.client.render.AzRendererPipelineContext;
import com.blib.internal.client.texture.AnimatableTexture;

public class AzItemRendererPipeline extends AzRendererPipeline<UUID, ItemStack> {

    private final AzItemRenderer itemRenderer;

    protected Matrix4f itemRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzItemRendererPipeline(AzItemRendererConfig config, AzItemRenderer itemRenderer) {
        super(config);
        this.itemRenderer = itemRenderer;
    }

    @Override
    protected AzRendererPipelineContext<UUID, ItemStack> createContext(
        AzRendererPipeline<UUID, ItemStack> rendererPipeline
    ) {
        return config.pipelineContext(this);
    }

    @Override
    protected AzItemModelRenderer createModelRenderer(AzLayerRenderer<UUID, ItemStack> layerRenderer) {
        return (AzItemModelRenderer) config.modelRendererProvider(this, layerRenderer);
    }

    @Override
    protected AzLayerRenderer<UUID, ItemStack> createLayerRenderer(AzRendererConfig<UUID, ItemStack> config) {
        return new AzLayerRenderer<>(config::renderLayers);
    }

    @Override
    public void preRender(AzRendererPipelineContext<UUID, ItemStack> context, boolean isReRender) {
        var itemContext = (AzItemRendererPipelineContext) context;
        var poseStack = itemContext.poseStack();
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

        var config = itemRenderer.config();
        var scaleWidth = config.scaleWidth(context.animatable());
        var scaleHeight = config.scaleHeight(context.animatable());
        scaleModelForRender(itemContext, scaleWidth, scaleHeight, isReRender);

        if (!isReRender) {
            var useNewOffset = config.useNewOffset();
            poseStack.translate(0.5f, useNewOffset ? 0.0f : 0.51f, 0.5f);
        }

        // If the item model has the leftArm or rightArm bone, hide them.
        Stream.of("leftArm", "rightArm")
            .forEach(
                boneName -> context
                    .bakedModel()
                    .getBone(boneName)
                    .ifPresent(bone -> {
                        bone.setHidden(true);
                        bone.setChildrenHidden(false);
                    })
            );

        if (config.alpha(context.animatable()) < 1) {
            var alpha = (int) (config.alpha(context.animatable()) * 0xFF) << 24;
            var color = (itemContext.renderColor() & 0xFFFFFF) | alpha;
            itemContext.setRenderColor(color);
            itemContext.setTranslucent(true);
        }
        config.preRenderEntry(context);
    }

    @Override
    public void postRender(AzRendererPipelineContext<UUID, ItemStack> context, boolean isReRender) {
        config.postRenderEntry(context);
        context.setTextureOverride(null);
    }

    @Override
    public void updateAnimatedTextureFrame(ItemStack animatable) {
        AnimatableTexture.setAndUpdate(config.textureLocation(context().currentEntity(), animatable));
    }

    public void setModelRenderTranslations(Matrix4f modelRenderTranslations) {
        this.modelRenderTranslations = modelRenderTranslations;
    }

    public Matrix4f getItemRenderTranslations() {
        return itemRenderTranslations;
    }

    public Matrix4f getModelRenderTranslations() {
        return modelRenderTranslations;
    }

    public AzItemRenderer getRenderer() {
        return itemRenderer;
    }
}

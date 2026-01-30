package com.blib.api.client.render.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.common.color.v1.Color;
import com.blib.internal.client.render.util.RenderUtil;

public abstract class AzRendererPipelineContext<K, T> {

    public ResourceLocation textureOverride;

    private final AzRendererPipeline<K, T> rendererPipeline;

    protected T animatable;

    protected @Nullable Entity currentEntity;

    private AzBakedModel bakedModel;

    private MultiBufferSource multiBufferSource;

    private int packedLight;

    private int packedOverlay;

    private float partialTick;

    private PoseStack poseStack;

    private int renderColor;

    private @Nullable RenderType renderType;

    private VertexConsumer vertexConsumer;

    protected static final Map<ResourceLocation, IntIntPair> TEXTURE_DIMENSIONS_CACHE =
        new Object2ObjectOpenHashMap<>();

    protected AzRendererPipelineContext(AzRendererPipeline<K, T> rendererPipeline) {
        this.rendererPipeline = rendererPipeline;
    }

    public void populate(
        T animatable,
        AzBakedModel bakedModel,
        MultiBufferSource multiBufferSource,
        int packedLight,
        float partialTick,
        PoseStack poseStack,
        RenderType renderType,
        VertexConsumer vertexConsumer
    ) {
        this.animatable = animatable;
        this.bakedModel = bakedModel;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.renderType = renderType;
        this.vertexConsumer = vertexConsumer;
        this.renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();

        if (renderType == null) {
            var textureLocation = rendererPipeline.config().textureLocation(currentEntity, animatable);
            this.renderType = getDefaultRenderType(
                animatable,
                textureLocation,
                multiBufferSource,
                partialTick,
                rendererPipeline.config().getRenderType(currentEntity, animatable),
                rendererPipeline.config().alpha(animatable)
            );
        }

        if (vertexConsumer == null && this.renderType != null) {
            this.vertexConsumer = multiBufferSource.getBuffer(this.renderType);
        }
    }

    public abstract RenderType getDefaultRenderType(
        T animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick,
        RenderType defaultRenderType,
        float alpha
    );

    protected Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.WHITE;
    }

    protected int getPackedOverlay(T animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    public AzRendererPipeline<K, T> rendererPipeline() {
        return rendererPipeline;
    }

    public T animatable() {
        return animatable;
    }

    public void setCurrentEntity(Entity currentEntity) {
        this.currentEntity = currentEntity;
    }

    public @Nullable Entity currentEntity() {
        return currentEntity;
    }

    public AzBakedModel bakedModel() {
        return bakedModel;
    }

    public MultiBufferSource multiBufferSource() {
        return multiBufferSource;
    }

    public int packedLight() {
        return packedLight;
    }

    public void setPackedLight(int packedLight) {
        this.packedLight = packedLight;
    }

    public int packedOverlay() {
        return packedOverlay;
    }

    public void setPackedOverlay(int packedOverlay) {
        this.packedOverlay = packedOverlay;
    }

    public float partialTick() {
        return partialTick;
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public int renderColor() {
        return renderColor;
    }

    public void setRenderColor(int renderColor) {
        this.renderColor = renderColor;
    }

    public @Nullable RenderType renderType() {
        return renderType;
    }

    public void setRenderType(@Nullable RenderType renderType) {
        this.renderType = renderType;
    }

    public VertexConsumer vertexConsumer() {
        return vertexConsumer;
    }

    public void setVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    public void setTextureOverride(ResourceLocation textureOverride) {
        this.textureOverride = textureOverride;
    }

    public ResourceLocation getTextureOverride() {
        return textureOverride;
    }

    public IntIntPair computeTextureSize(ResourceLocation texture) {
        return TEXTURE_DIMENSIONS_CACHE.computeIfAbsent(texture, RenderUtil::getTextureDimensions);
    }
}

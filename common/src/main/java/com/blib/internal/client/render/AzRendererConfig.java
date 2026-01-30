package com.blib.internal.client.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.render.v1.layer.AzRenderLayer;
import com.blib.internal.client.model.AzBone;

public class AzRendererConfig<K, T> {

    private final Supplier<@Nullable AzAnimator<K, T>> animatorProvider;

    private final BiFunction<@Nullable Entity, T, ResourceLocation> modelLocationProvider;

    private final BiFunction<AzRendererPipeline<K, T>, AzLayerRenderer<K, T>, AzModelRenderer<K, T>> modelRendererProvider;

    private final Function<AzRendererPipeline<K, T>, AzRendererPipelineContext<K, T>> pipelineContextFunction;

    private final BiFunction<@Nullable Entity, T, RenderType> renderTypeFunction;

    private final Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> preRenderEntry;

    private final Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> renderEntry;

    private final Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> postRenderEntry;

    private final List<AzRenderLayer<K, T>> renderLayers;

    private final BiFunction<@Nullable Entity, T, ResourceLocation> textureLocationProvider;

    private final Function<T, Float> alphaFunction;

    private final Function<T, Float> scaleHeight;

    private final Function<T, Float> scaleWidth;

    private final @Nullable Function<AzBone, ResourceLocation> boneTextureOverrideProvider;

    private final @Nullable Function<AzBone, RenderType> boneRenderTypeOverrideProvider;

    public AzRendererConfig(
        Supplier<AzAnimator<K, T>> animatorProvider,
        BiFunction<@Nullable Entity, T, ResourceLocation> modelLocationProvider,
        BiFunction<AzRendererPipeline<K, T>, AzLayerRenderer<K, T>, AzModelRenderer<K, T>> modelRendererProvider,
        Function<AzRendererPipeline<K, T>, AzRendererPipelineContext<K, T>> pipelineContextFunction,
        BiFunction<@Nullable Entity, T, RenderType> renderTypeFunction,
        List<AzRenderLayer<K, T>> renderLayers,
        Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> preRenderEntry,
        Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> renderEntry,
        Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> postRenderEntry,
        BiFunction<@Nullable Entity, T, ResourceLocation> textureLocationProvider,
        Function<T, Float> alphaFunction,
        Function<T, Float> scaleHeight,
        Function<T, Float> scaleWidth,
        Function<AzBone, ResourceLocation> boneTextureOverrideProvider,
        Function<AzBone, RenderType> boneRenderTypeOverrideProvider
    ) {
        this.animatorProvider = animatorProvider;
        this.modelLocationProvider = modelLocationProvider;
        this.modelRendererProvider = modelRendererProvider;
        this.pipelineContextFunction = pipelineContextFunction;
        this.renderTypeFunction = renderTypeFunction;
        this.renderLayers = Collections.unmodifiableList(renderLayers);
        this.preRenderEntry = preRenderEntry;
        this.renderEntry = renderEntry;
        this.postRenderEntry = postRenderEntry;
        this.textureLocationProvider = textureLocationProvider;
        this.alphaFunction = alphaFunction;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        this.boneTextureOverrideProvider = boneTextureOverrideProvider;
        this.boneRenderTypeOverrideProvider = boneRenderTypeOverrideProvider;
    }

    public @Nullable AzAnimator<K, T> createAnimator() {
        return animatorProvider.get();
    }

    public ResourceLocation modelLocation(T animatable) {
        return modelLocation(null, animatable);
    }

    public ResourceLocation modelLocation(@Nullable Entity entity, T animatable) {
        return modelLocationProvider.apply(entity, animatable);
    }

    public AzRendererPipelineContext<K, T> pipelineContext(AzRendererPipeline<K, T> pipeline) {
        return pipelineContextFunction.apply(pipeline);
    }

    public ResourceLocation textureLocation(T animatable) {
        return textureLocation(null, animatable);
    }

    public ResourceLocation textureLocation(@Nullable Entity entity, T animatable) {
        return textureLocationProvider.apply(entity, animatable);
    }

    public AzModelRenderer<K, T> modelRendererProvider(
        AzRendererPipeline<K, T> pipeline,
        AzLayerRenderer<K, T> layerRenderer
    ) {
        return modelRendererProvider.apply(pipeline, layerRenderer);
    }

    public RenderType getRenderType(T animatable) {
        return getRenderType(null, animatable);
    }

    public RenderType getRenderType(@Nullable Entity entity, T animatable) {
        return renderTypeFunction.apply(entity, animatable);
    }

    public List<AzRenderLayer<K, T>> renderLayers() {
        return renderLayers;
    }

    public AzRendererPipelineContext<K, T> preRenderEntry(AzRendererPipelineContext<K, T> animatable) {
        return preRenderEntry.apply(animatable);
    }

    public AzRendererPipelineContext<K, T> renderEntry(AzRendererPipelineContext<K, T> animatable) {
        return renderEntry.apply(animatable);
    }

    public AzRendererPipelineContext<K, T> postRenderEntry(AzRendererPipelineContext<K, T> animatable) {
        return postRenderEntry.apply(animatable);
    }

    public float alpha(T entity) {
        return alphaFunction.apply(entity);
    }

    public float scaleHeight(T entity) {
        return scaleHeight.apply(entity);
    }

    public float scaleWidth(T entity) {
        return scaleWidth.apply(entity);
    }

    public @Nullable ResourceLocation boneTextureOverrideProvider(AzBone bone) {
        return boneTextureOverrideProvider.apply(bone);
    }

    public @Nullable RenderType boneRenderTypeOverrideProvider(AzBone bone) {
        return boneRenderTypeOverrideProvider.apply(bone);
    }

    public static class Builder<K, T> {

        protected final BiFunction<@Nullable Entity, T, ResourceLocation> modelLocationProvider;

        protected BiFunction<AzRendererPipeline<K, T>, AzLayerRenderer<K, T>, AzModelRenderer<K, T>> modelRendererProvider;

        protected Function<AzRendererPipeline<K, T>, AzRendererPipelineContext<K, T>> pipelineContextFunction;

        protected BiFunction<@Nullable Entity, T, RenderType> renderTypeProvider;

        public final List<AzRenderLayer<K, T>> renderLayers;

        protected Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> preRenderEntry;

        protected Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> renderEntry;

        protected Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> postRenderEntry;

        protected final BiFunction<@Nullable Entity, T, ResourceLocation> textureLocationProvider;

        protected Supplier<@Nullable AzAnimator<K, T>> animatorProvider;

        protected Function<T, Float> alphaFunction;

        protected Function<T, Float> scaleHeight;

        protected Function<T, Float> scaleWidth;

        private @Nullable Function<AzBone, ResourceLocation> boneTextureOverrideProvider;

        private @Nullable Function<AzBone, RenderType> boneRenderTypeOverrideProvider;

        protected Builder(
            BiFunction<@Nullable Entity, T, ResourceLocation> modelLocationProvider,
            BiFunction<@Nullable Entity, T, ResourceLocation> textureLocationProvider
        ) {
            this.animatorProvider = () -> null;
            this.modelLocationProvider = modelLocationProvider;
            this.modelRendererProvider = AzModelRenderer::new;
            this.pipelineContextFunction = null;
            this.renderTypeProvider = (a, b) -> RenderType.entityCutout(textureLocationProvider.apply(a, b));
            this.renderLayers = new ObjectArrayList<>();
            this.preRenderEntry = $ -> $;
            this.renderEntry = $ -> $;
            this.postRenderEntry = $ -> $;
            this.textureLocationProvider = textureLocationProvider;
            this.alphaFunction = $ -> 1.0F;
            this.scaleHeight = $ -> 1.0F;
            this.scaleWidth = $ -> 1.0F;
            this.boneTextureOverrideProvider = $ -> null;
            this.boneRenderTypeOverrideProvider = $ -> null;
        }

        public Builder<K, T> setBoneTextureOverrideProvider(
            Function<AzBone, ResourceLocation> boneTextureOverrideProvider
        ) {
            this.boneTextureOverrideProvider = boneTextureOverrideProvider;
            return this;
        }

        public Builder<K, T> setBoneRenderTypeOverrideProvider(
            Function<AzBone, RenderType> boneRenderTypeOverrideProvider
        ) {
            this.boneRenderTypeOverrideProvider = boneRenderTypeOverrideProvider;
            return this;
        }

        public Builder<K, T> setModelRenderer(
            BiFunction<AzRendererPipeline<K, T>, AzLayerRenderer<K, T>, AzModelRenderer<K, T>> modelRendererProvider
        ) {
            this.modelRendererProvider = modelRendererProvider;
            return this;
        }

        public Builder<K, T> setPipelineContext(
            Function<AzRendererPipeline<K, T>, AzRendererPipelineContext<K, T>> pipelineContextFunction
        ) {
            this.pipelineContextFunction = pipelineContextFunction;
            return this;
        }

        public Builder<K, T> setPrerenderEntry(
            Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> preRenderEntry
        ) {
            this.preRenderEntry = preRenderEntry;
            return this;
        }

        public Builder<K, T> setRenderEntry(
            Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> renderEntry
        ) {
            this.renderEntry = renderEntry;
            return this;
        }

        public Builder<K, T> setPostRenderEntry(
            Function<AzRendererPipelineContext<K, T>, AzRendererPipelineContext<K, T>> postRenderEntry
        ) {
            this.postRenderEntry = postRenderEntry;
            return this;
        }

        public Builder<K, T> setAnimatorProvider(Supplier<@Nullable AzAnimator<K, T>> animatorProvider) {
            this.animatorProvider = animatorProvider;
            return this;
        }

        public Builder<K, T> addRenderLayer(AzRenderLayer<K, T> renderLayer) {
            this.renderLayers.add(renderLayer);
            return this;
        }

        public Builder<K, T> setAlpha(Function<T, Float> alphaFunction) {
            this.alphaFunction = alphaFunction;
            return this;
        }

        public Builder<K, T> setAlpha(float alpha) {
            this.alphaFunction = $ -> alpha;
            return this;
        }

        public Builder<K, T> setScale(float scale) {
            return setScale(scale, scale);
        }

        public Builder<K, T> setScale(float scaleWidth, float scaleHeight) {
            this.scaleHeight = $ -> scaleHeight;
            this.scaleWidth = $ -> scaleWidth;
            return this;
        }

        public Builder<K, T> setScale(Function<T, Float> scaleFunction) {
            this.scaleHeight = scaleFunction;
            this.scaleWidth = scaleFunction;
            return this;
        }

        public Builder<K, T> setScale(Function<T, Float> scaleHeightFunction, Function<T, Float> scaleWidthFunction) {
            this.scaleHeight = scaleHeightFunction;
            this.scaleWidth = scaleWidthFunction;
            return this;
        }

        public AzRendererConfig<K, T> build() {
            return new AzRendererConfig<>(
                animatorProvider,
                modelLocationProvider,
                modelRendererProvider,
                pipelineContextFunction,
                renderTypeProvider,
                renderLayers,
                preRenderEntry,
                renderEntry,
                postRenderEntry,
                textureLocationProvider,
                alphaFunction,
                scaleHeight,
                scaleWidth,
                boneTextureOverrideProvider,
                boneRenderTypeOverrideProvider
            );
        }
    }
}

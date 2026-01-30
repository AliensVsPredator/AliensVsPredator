package com.blib.api.client.render.v1.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererConfig;
import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.entity.model.AzEntityModelRenderer;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipelineContext;
import com.blib.api.client.render.v1.layer.AzRenderLayer;

public class AzEntityRendererConfig<T extends Entity> extends AzRendererConfig<UUID, T> {

    private final Function<T, Float> deathMaxRotationProvider;

    private final Function<T, Float> shadowRadius;

    private AzEntityRendererConfig(
        Supplier<AzAnimator<UUID, T>> animatorProvider,
        Function<T, Float> deathMaxRotationProvider,
        Function<T, Float> shadowRadius,
        Function<T, RenderType> renderTypeFunction,
        Function<T, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<UUID, T>> renderLayers,
        Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> preRenderEntry,
        Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> renderEntry,
        Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> postRenderEntry,
        Function<T, ResourceLocation> textureLocationProvider,
        Function<T, Float> alphaFunction,
        Function<T, Float> scaleHeight,
        Function<T, Float> scaleWidth,
        BiFunction<AzRendererPipeline<UUID, T>, AzLayerRenderer<UUID, T>, AzModelRenderer<UUID, T>> modelRendererProvider,
        Function<AzRendererPipeline<UUID, T>, AzRendererPipelineContext<UUID, T>> pipelineContextFunction,
        Function<AzBone, ResourceLocation> boneTextureOverrideProvider,
        Function<AzBone, RenderType> boneRenderTypeOverrideProvider
    ) {
        super(
            animatorProvider,
            (a, b) -> modelLocationProvider.apply(b),
            modelRendererProvider,
            pipelineContextFunction,
            (a, b) -> renderTypeFunction.apply(b),
            renderLayers,
            preRenderEntry,
            renderEntry,
            postRenderEntry,
            (a, b) -> textureLocationProvider.apply(b),
            alphaFunction,
            scaleHeight,
            scaleWidth,
            boneTextureOverrideProvider,
            boneRenderTypeOverrideProvider
        );
        this.deathMaxRotationProvider = deathMaxRotationProvider;
        this.shadowRadius = shadowRadius;
    }

    public float getDeathMaxRotation(T entity) {
        return deathMaxRotationProvider.apply(entity);
    }

    public float shadowRadius(T entity) {
        return shadowRadius.apply(entity);
    }

    public static <T extends Entity> Builder<T> builder(
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
    ) {
        return new Builder<>($ -> modelLocation, $ -> textureLocation);
    }

    public static <T extends Entity> Builder<T> builder(
        Function<T, ResourceLocation> modelLocationProvider,
        Function<T, ResourceLocation> textureLocationProvider
    ) {
        return new Builder<>(modelLocationProvider, textureLocationProvider);
    }

    public static class Builder<T extends Entity> extends AzRendererConfig.Builder<UUID, T> {

        private Function<T, Float> deathMaxRotationProvider;

        protected Function<T, Float> shadowRadius;

        public Builder(
            Function<T, ResourceLocation> modelLocationProvider,
            Function<T, ResourceLocation> textureLocationProvider
        ) {
            super((a, b) -> modelLocationProvider.apply(b), (a, b) -> textureLocationProvider.apply(b));
            this.modelRendererProvider = (entityRendererPipeline, layer) -> new AzEntityModelRenderer<>(
                (AzEntityRendererPipeline<T>) entityRendererPipeline,
                layer
            );
            this.pipelineContextFunction = AzEntityRendererPipelineContext::new;
            this.renderTypeProvider = (a, b) -> RenderType.entityCutout(textureLocationProvider.apply(b));
            this.deathMaxRotationProvider = $ -> 90F;
            this.shadowRadius = $ -> 0.0F;
        }

        @Override
        public Builder<T> setBoneRenderTypeOverrideProvider(
            Function<AzBone, RenderType> boneRenderTypeOverrideProvider
        ) {
            return (Builder<T>) super.setBoneRenderTypeOverrideProvider(boneRenderTypeOverrideProvider);
        }

        @Override
        public Builder<T> setBoneTextureOverrideProvider(
            Function<AzBone, ResourceLocation> boneTextureOverrideProvider
        ) {
            return (Builder<T>) super.setBoneTextureOverrideProvider(boneTextureOverrideProvider);
        }

        @Override
        public Builder<T> setModelRenderer(
            BiFunction<AzRendererPipeline<UUID, T>, AzLayerRenderer<UUID, T>, AzModelRenderer<UUID, T>> modelRendererProvider
        ) {
            return (Builder<T>) super.setModelRenderer(modelRendererProvider);
        }

        @Override
        public Builder<T> setPipelineContext(
            Function<AzRendererPipeline<UUID, T>, AzRendererPipelineContext<UUID, T>> azRendererPipelineAzRendererPipelineContextFunction
        ) {
            return (Builder<T>) super.setPipelineContext(azRendererPipelineAzRendererPipelineContextFunction);
        }

        @Override
        public Builder<T> addRenderLayer(AzRenderLayer<UUID, T> renderLayer) {
            return (Builder<T>) super.addRenderLayer(renderLayer);
        }

        public Builder<T> setRenderType(RenderType renderType) {
            this.renderTypeProvider = (a, b) -> renderType;
            return this;
        }

        public Builder<T> setRenderType(Function<T, RenderType> renderTypeProvider) {
            this.renderTypeProvider = (a, b) -> renderTypeProvider.apply(b);
            return this;
        }

        public Builder<T> setRenderType(BiFunction<Entity, T, RenderType> renderTypeProvider) {
            this.renderTypeProvider = renderTypeProvider;
            return this;
        }

        @Override
        public Builder<T> setPrerenderEntry(
            Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> preRenderEntry
        ) {
            return (AzEntityRendererConfig.Builder<T>) super.setPrerenderEntry(preRenderEntry);
        }

        @Override
        public Builder<T> setRenderEntry(
            Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> renderEntry
        ) {
            return (AzEntityRendererConfig.Builder<T>) super.setRenderEntry(renderEntry);
        }

        @Override
        public Builder<T> setPostRenderEntry(
            Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> preRenderEntry
        ) {
            return (AzEntityRendererConfig.Builder<T>) super.setPostRenderEntry(preRenderEntry);
        }

        @Override
        public Builder<T> setAnimatorProvider(Supplier<@Nullable AzAnimator<UUID, T>> animatorProvider) {
            return (Builder<T>) super.setAnimatorProvider(animatorProvider);
        }

        public Builder<T> setDeathMaxRotation(float angle) {
            this.deathMaxRotationProvider = $ -> angle;
            return this;
        }

        @Override
        public Builder<T> setAlpha(Function<T, Float> alphaFunction) {
            return (AzEntityRendererConfig.Builder<T>) super.setAlpha(alphaFunction);
        }

        @Override
        public Builder<T> setAlpha(float alpha) {
            return (AzEntityRendererConfig.Builder<T>) super.setAlpha(alpha);
        }

        @Override
        public Builder<T> setScale(Function<T, Float> scaleFunction) {
            return (AzEntityRendererConfig.Builder<T>) super.setScale(scaleFunction);
        }

        @Override
        public Builder<T> setScale(Function<T, Float> scaleHeightFunction, Function<T, Float> scaleWidthFunction) {
            return (AzEntityRendererConfig.Builder<T>) super.setScale(scaleHeightFunction, scaleWidthFunction);
        }

        @Override
        public Builder<T> setScale(float scale) {
            return (AzEntityRendererConfig.Builder<T>) super.setScale(scale);
        }

        @Override
        public Builder<T> setScale(float scaleWidth, float scaleHeight) {
            return (AzEntityRendererConfig.Builder<T>) super.setScale(scaleWidth, scaleHeight);
        }

        public Builder<T> setDeathMaxRotation(Function<T, Float> deathMaxRotationProvider) {
            this.deathMaxRotationProvider = deathMaxRotationProvider;
            return this;
        }

        public Builder<T> setShadowRadius(Function<T, Float> shadowRadiusFunction) {
            this.shadowRadius = shadowRadiusFunction;
            return this;
        }

        public Builder<T> setShadowRadius(float shadowRadius) {
            this.shadowRadius = $ -> shadowRadius;
            return this;
        }

        @Override
        public AzEntityRendererConfig<T> build() {
            var baseConfig = super.build();

            return new AzEntityRendererConfig<>(
                baseConfig::createAnimator,
                deathMaxRotationProvider,
                shadowRadius,
                baseConfig::getRenderType,
                baseConfig::modelLocation,
                baseConfig.renderLayers(),
                baseConfig::preRenderEntry,
                baseConfig::renderEntry,
                baseConfig::postRenderEntry,
                baseConfig::textureLocation,
                baseConfig::alpha,
                baseConfig::scaleHeight,
                baseConfig::scaleWidth,
                baseConfig::modelRendererProvider,
                baseConfig::pipelineContext,
                baseConfig::boneTextureOverrideProvider,
                baseConfig::boneRenderTypeOverrideProvider
            );
        }
    }
}

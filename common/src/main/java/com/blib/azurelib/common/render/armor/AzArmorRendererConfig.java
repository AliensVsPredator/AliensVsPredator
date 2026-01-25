package com.blib.azurelib.common.render.armor;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.model.AzBone;
import com.blib.azurelib.common.render.*;
import com.blib.azurelib.common.render.AzRendererPipelineContext;
import com.blib.azurelib.common.render.armor.bone.AzArmorBoneProvider;
import com.blib.azurelib.common.render.armor.bone.AzDefaultArmorBoneProvider;
import com.blib.azurelib.common.render.layer.AzRenderLayer;

public class AzArmorRendererConfig extends com.blib.azurelib.common.render.AzRendererConfig<UUID, ItemStack> {

    private final AzArmorBoneProvider boneProvider;

    private AzArmorRendererConfig(
        Supplier<AzAnimator<UUID, ItemStack>> animatorProvider,
        AzArmorBoneProvider boneProvider,
        BiFunction<Entity, ItemStack, RenderType> renderTypeProvider,
        BiFunction<Entity, ItemStack, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<UUID, ItemStack>> renderLayers,
        Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> preRenderEntry,
        Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> renderEntry,
        Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> postRenderEntry,
        BiFunction<Entity, ItemStack, ResourceLocation> textureLocationProvider,
        Function<ItemStack, Float> alphaFunction,
        Function<ItemStack, Float> scaleHeight,
        Function<ItemStack, Float> scaleWidth,
        BiFunction<com.blib.azurelib.common.render.AzRendererPipeline<UUID, ItemStack>, com.blib.azurelib.common.render.AzLayerRenderer<UUID, ItemStack>, com.blib.azurelib.common.render.AzModelRenderer<UUID, ItemStack>> modelRendererProvider,
        Function<com.blib.azurelib.common.render.AzRendererPipeline<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> pipelineContextFunction,
        Function<AzBone, ResourceLocation> boneTextureOverrideProvider,
        Function<AzBone, RenderType> boneRenderTypeOverrideProvider
    ) {
        super(
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
        this.boneProvider = boneProvider;
    }

    public AzArmorBoneProvider boneProvider() {
        return boneProvider;
    }

    public static Builder builder(
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
    ) {
        return new Builder((a, b) -> modelLocation, (a, b) -> textureLocation);
    }

    public static Builder builder(
        BiFunction<Entity, ItemStack, ResourceLocation> modelLocationProvider,
        BiFunction<Entity, ItemStack, ResourceLocation> textureLocationProvider
    ) {
        return new Builder(modelLocationProvider, textureLocationProvider);
    }

    public static class Builder extends com.blib.azurelib.common.render.AzRendererConfig.Builder<UUID, ItemStack> {

        private AzArmorBoneProvider boneProvider;

        protected Builder(
            BiFunction<Entity, ItemStack, ResourceLocation> modelLocationProvider,
            BiFunction<Entity, ItemStack, ResourceLocation> textureLocationProvider
        ) {
            super(modelLocationProvider, textureLocationProvider);
            this.boneProvider = new AzDefaultArmorBoneProvider();
            this.modelRendererProvider = (entityRendererPipeline, layer) -> new AzArmorModelRenderer(
                (AzArmorRendererPipeline) entityRendererPipeline,
                layer
            );
            this.pipelineContextFunction = AzArmorRendererPipelineContext::new;
            this.renderTypeProvider = (a, b) -> RenderType.armorCutoutNoCull(textureLocationProvider.apply(a, b));
        }

        @Override
        public Builder setBoneRenderTypeOverrideProvider(Function<AzBone, RenderType> boneRenderTypeOverrideProvider) {
            return (Builder) super.setBoneRenderTypeOverrideProvider(boneRenderTypeOverrideProvider);
        }

        @Override
        public Builder setBoneTextureOverrideProvider(Function<AzBone, ResourceLocation> boneTextureOverrideProvider) {
            return (Builder) super.setBoneTextureOverrideProvider(boneTextureOverrideProvider);
        }

        @Override
        public Builder setModelRenderer(
            BiFunction<com.blib.azurelib.common.render.AzRendererPipeline<UUID, ItemStack>, com.blib.azurelib.common.render.AzLayerRenderer<UUID, ItemStack>, com.blib.azurelib.common.render.AzModelRenderer<UUID, ItemStack>> modelRendererProvider
        ) {
            return (Builder) super.setModelRenderer(modelRendererProvider);
        }

        @Override
        public Builder setPipelineContext(
            Function<com.blib.azurelib.common.render.AzRendererPipeline<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> azRendererPipelineAzRendererPipelineContextFunction
        ) {
            return (Builder) super.setPipelineContext(azRendererPipelineAzRendererPipelineContextFunction);
        }

        @Override
        public Builder addRenderLayer(AzRenderLayer<UUID, ItemStack> renderLayer) {
            return (Builder) super.addRenderLayer(renderLayer);
        }

        public Builder setRenderType(RenderType renderType) {
            this.renderTypeProvider = (a, b) -> renderType;
            return this;
        }

        public Builder setRenderType(BiFunction<Entity, ItemStack, RenderType> renderTypeProvider) {
            this.renderTypeProvider = renderTypeProvider;
            return this;
        }

        @Override
        public Builder setPrerenderEntry(
            Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> preRenderEntry
        ) {
            return (AzArmorRendererConfig.Builder) super.setPrerenderEntry(preRenderEntry);
        }

        @Override
        public Builder setRenderEntry(
            Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>> renderEntry
        ) {
            return (AzArmorRendererConfig.Builder) super.setRenderEntry(renderEntry);
        }

        @Override
        public Builder setPostRenderEntry(
            Function<com.blib.azurelib.common.render.AzRendererPipelineContext<UUID, ItemStack>, AzRendererPipelineContext<UUID, ItemStack>> preRenderEntry
        ) {
            return (AzArmorRendererConfig.Builder) super.setPostRenderEntry(preRenderEntry);
        }

        @Override
        public Builder setAnimatorProvider(Supplier<@Nullable AzAnimator<UUID, ItemStack>> animatorProvider) {
            return (Builder) super.setAnimatorProvider(animatorProvider);
        }

        public Builder setBoneProvider(AzArmorBoneProvider boneProvider) {
            this.boneProvider = boneProvider;
            return this;
        }

        @Override
        public Builder setAlpha(Function<ItemStack, Float> alphaFunction) {
            return (AzArmorRendererConfig.Builder) super.setAlpha(alphaFunction);
        }

        @Override
        public Builder setAlpha(float alpha) {
            return (AzArmorRendererConfig.Builder) super.setAlpha(alpha);
        }

        @Override
        public Builder setScale(Function<ItemStack, Float> scaleFunction) {
            return (AzArmorRendererConfig.Builder) super.setScale(scaleFunction);
        }

        @Override
        public Builder setScale(
            Function<ItemStack, Float> scaleHeightFunction,
            Function<ItemStack, Float> scaleWidthFunction
        ) {
            return (AzArmorRendererConfig.Builder) super.setScale(scaleHeightFunction, scaleWidthFunction);
        }

        @Override
        public Builder setScale(float scale) {
            return (AzArmorRendererConfig.Builder) super.setScale(scale);
        }

        @Override
        public Builder setScale(float scaleWidth, float scaleHeight) {
            return (AzArmorRendererConfig.Builder) super.setScale(scaleWidth, scaleHeight);
        }

        @Override
        public AzArmorRendererConfig build() {
            var baseConfig = super.build();

            return new AzArmorRendererConfig(
                baseConfig::createAnimator,
                boneProvider,
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

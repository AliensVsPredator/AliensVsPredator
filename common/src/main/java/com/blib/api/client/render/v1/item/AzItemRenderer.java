package com.blib.api.client.render.v1.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.api.client.animation.v1.animator.AzItemAnimator;
import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipeline;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.blib.internal.client.render.AzProvider;
import com.blib.internal.client.render.item.AzItemGuiRenderUtil;
import com.blib.mod.common.registry.init.BLibDataComponents;

public abstract class AzItemRenderer {

    private final AzItemRendererConfig config;

    private final AzProvider<UUID, ItemStack> provider;

    public final AzItemRendererPipeline rendererPipeline;

    @Nullable
    private AzItemAnimator reusedAzItemAnimator;

    protected AzItemRenderer(
        AzItemRendererConfig config
    ) {
        this.rendererPipeline = createPipeline(config);
        this.provider = new AzProvider<>(
            config::createAnimator,
            config::modelLocation,
            animator -> animator.get(BLibDataComponents.AZ_ID.get())
        );
        this.config = config;
    }

    protected AzItemRendererPipeline createPipeline(AzItemRendererConfig config) {
        return new AzItemRendererPipeline(config, this);
    }

    public void renderByGui(
        ItemStack stack,
        ItemDisplayContext transformType,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource source,
        int packedLight
    ) {
        var context = rendererPipeline.context();
        var model = provider.provideBakedModel(context.currentEntity(), stack);
        var itemContext = (AzItemRendererPipelineContext) context;

        itemContext.setTransformType(transformType);

        prepareAnimator(stack, model);

        AzItemGuiRenderUtil.renderInGui(config, rendererPipeline, stack, model, stack, poseStack, source, packedLight);
    }

    public void renderByItem(
        ItemStack stack,
        ItemDisplayContext transformType,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource source,
        int packedLight
    ) {
        var context = rendererPipeline.context();
        var model = provider.provideBakedModel(context.currentEntity(), stack);
        var partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        var textureLocation = config.textureLocation(context.currentEntity(), stack);
        var renderType = rendererPipeline.context()
            .getDefaultRenderType(
                stack,
                textureLocation,
                source,
                partialTick,
                config.getRenderType(context.currentEntity(), stack),
                config.alpha(stack)
            );
        // TODO: Why the null check here?
        var withGlint = stack != null && stack.hasFoil();
        var buffer = ItemRenderer.getFoilBufferDirect(source, renderType, false, withGlint);
        var itemContext = (AzItemRendererPipelineContext) context;

        itemContext.setTransformType(transformType);

        prepareAnimator(stack, model);

        rendererPipeline.render(poseStack, model, stack, source, renderType, buffer, 0, partialTick, packedLight);
    }

    private void prepareAnimator(ItemStack stack, AzBakedModel model) {
        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzItemAnimator = (AzItemAnimator) provider.provideAnimator(
            rendererPipeline.context().currentEntity(),
            stack
        );
    }

    public @Nullable AzItemAnimator getAnimator() {
        return reusedAzItemAnimator;
    }

    public AzItemRendererConfig config() {
        return config;
    }
}

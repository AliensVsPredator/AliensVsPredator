package com.blib.internal.client.render.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.render.v1.item.AzItemRendererConfig;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipeline;

public class AzItemGuiRenderUtil {

    public static void renderInGui(
        AzItemRendererConfig config,
        AzItemRendererPipeline rendererPipeline,
        ItemStack stack,
        AzBakedModel model,
        ItemStack currentItemStack,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight
    ) {
        if (config.useEntityGuiLighting()) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }

        var context = rendererPipeline.context();
        var partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        var bSource =
            source instanceof MultiBufferSource.BufferSource bufferSource
                ? bufferSource
                : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        var textureLocation = config.textureLocation(context.currentEntity(), stack);
        var renderType = rendererPipeline.context()
            .getDefaultRenderType(
                stack,
                textureLocation,
                bSource,
                partialTick,
                config.getRenderType(context.currentEntity(), stack),
                config.alpha(stack)
            );
        var withGlint = currentItemStack != null && currentItemStack.hasFoil();
        var buffer = ItemRenderer.getFoilBufferDirect(source, renderType, true, withGlint);

        poseStack.pushPose();

        rendererPipeline.render(poseStack, model, stack, bSource, renderType, buffer, 0, partialTick, packedLight);

        bSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();

        poseStack.popPose();
    }
}

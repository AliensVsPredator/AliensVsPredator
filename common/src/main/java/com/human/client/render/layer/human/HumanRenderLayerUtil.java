package com.human.client.render.layer.human;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HumanRenderLayerUtil {

    public static <T extends AbstractHuman> void applyColorWithInvisibility(
        AzRendererPipelineContext<UUID, T> context,
        @Nullable ResourceLocation textureLocation,
        int fallbackColor
    ) {
        var localPlayer = Minecraft.getInstance().player;

        if (textureLocation == null || localPlayer == null) {
            return;
        }

        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();

        var isInvisible = animatable.isInvisible();
        var renderType = isInvisible
            ? RenderType.entityTranslucentCull(textureLocation)
            : RenderType.entityCutout(textureLocation);
        var vertexConsumer = context.multiBufferSource().getBuffer(renderType);
        var alphaValue = animatable.isInvisibleTo(localPlayer) ? 0 : 0.38;
        int color;

        if (isInvisible) {
            var alpha = (int) (alphaValue * 0xFF) << 24;
            color = (context.renderColor() & 0xFFFFFF) | alpha;
        } else {
            color = fallbackColor;
        }

        context.setRenderColor(color);
        context.setVertexConsumer(vertexConsumer);

        renderPipeline.reRender(context);
    }

    private HumanRenderLayerUtil() {
        throw new UnsupportedOperationException();
    }
}

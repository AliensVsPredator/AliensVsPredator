package com.human.client.render.layer.human;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

public class HumanBeardLayer<T extends AbstractHuman> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();

        // TODO: Option usage here is suboptimal, null usage is preferred in hot paths like rendering code.
        animatable.getHumanFeatureManager()
            .getBeardTexture()
            .ifSome(beardTexture -> {
                var renderType = RenderType.entityCutout(beardTexture);
                var vertexConsumer = context.multiBufferSource().getBuffer(renderType);
                var previousColor = context.renderColor();

                context.setRenderColor(animatable.getHairColor());
                context.setVertexConsumer(vertexConsumer);

                renderPipeline.reRender(context);

                // make sure to reset the color at the end.
                context.setRenderColor(previousColor);
            });
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}

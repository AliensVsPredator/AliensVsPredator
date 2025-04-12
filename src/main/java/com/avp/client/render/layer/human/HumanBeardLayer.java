package com.avp.client.render.layer.human;

import com.avp.AVPResources;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;
import net.minecraft.resources.ResourceLocation;

public class HumanBeardLayer implements AzRenderLayer<MarineMob> {

    private final String HUMAN_TYPE;

    public HumanBeardLayer(String humanType) {
        HUMAN_TYPE = humanType;
    }

    @Override
    public void preRender(AzRendererPipelineContext<MarineMob> context) {}

    @Override
    public void render(AzRendererPipelineContext<MarineMob> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        if (Boolean.FALSE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            return;
        }
        if (Boolean.TRUE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            context.setVertexConsumer(
                context.multiBufferSource().getBuffer(RenderType.entityCutout(animatable.getBeardManager().getMaleBeardTexture(HUMAN_TYPE)))
            );
        }
        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone) {}

    public ResourceLocation getMaleBeardTexture(String humanType) {
        if (cachedMaleBeardTexture == null) {
            var random1 = this.entity.getRandom().nextIntBetweenInclusive(1, this.maxBeardTextures);
            int random2;

            if (random1 == 3) {
                random2 = 6;
            } else {
                random2 = this.entity.getSharedSecondRandomValue(6);
            }

            cachedMaleBeardTexture = AVPResources.entityTextureLocation(humanType + "_male_beard" + random1 + "_" + random2);
        }

        return cachedMaleBeardTexture;
    }
}

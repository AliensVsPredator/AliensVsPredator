package com.avp.client.render.entity;

import com.avp.AVPResources;
import com.avp.client.animation.MarineAnimator;
import com.avp.client.render.layer.*;
import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MarineRenderer extends AzEntityRenderer<MarineMob> {
    private static final String NAME = "marine";

    private static final ResourceLocation MALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_male");

    private static final ResourceLocation FEMALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_female");

    /**
     * TODO: Change texture to choose a random one when all are completed.
     */
    public MarineRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.builder(MarineRenderer::getModel, MarineRenderer::getTexture)
                .setAnimatorProvider(MarineAnimator::new)
                .addRenderLayer(new HumanArmorLayer<>())
                .addRenderLayer(new HumanHairLayer(NAME))
                .addRenderLayer(new HumanEyesLayer(NAME))
                .addRenderLayer(new HumanBeardLayer(NAME))
                .addRenderLayer(new HumanOutfitLayer(NAME))
                .addRenderLayer(new HumanItemLayer<>())
                .build(), context);
    }

    public static ResourceLocation getModel(MarineMob entity) {
        if (Boolean.TRUE.equals(entity.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            return MALE_MODEL;
        }
        return FEMALE_MODEL;
    }

    public static ResourceLocation getTexture(MarineMob entity) {
        if (Boolean.TRUE.equals(entity.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            return entity.getSkinManager().getMaleTexture(NAME);
        }
        return entity.getSkinManager().getFemaleTexture(NAME);
    }
}

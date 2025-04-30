package com.avp.fabric.client.render.entity;

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.fabric.AVPResources;
import com.avp.fabric.client.render.layer.human.HumanArmorLayer;
import com.avp.fabric.client.render.layer.human.HumanBeardLayer;
import com.avp.fabric.client.render.layer.human.HumanEyesLayer;
import com.avp.fabric.client.render.layer.human.HumanHairLayer;
import com.avp.fabric.client.render.layer.human.HumanItemLayer;
import com.avp.fabric.client.render.layer.human.HumanSkinLayer;
import com.avp.fabric.common.entity.living.human.AbstractHuman;

public abstract class AbstractHumanRenderer<T extends AbstractHuman> extends AzEntityRenderer<T> {

    private static final String NAME = "marine";

    private static final ResourceLocation MALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_male");

    private static final ResourceLocation FEMALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_female");

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation("human/base");

    public static <T extends AbstractHuman> ResourceLocation getModel(T entity) {
        return entity.isMale() ? MALE_MODEL : FEMALE_MODEL;
    }

    public static <T extends AbstractHuman> ResourceLocation getTexture(T entity) {
        return TEXTURE;
    }

    public AbstractHumanRenderer(EntityRendererProvider.Context context) {
        this(AbstractHumanRenderer.createConfig(), context);
    }

    public AbstractHumanRenderer(AzEntityRendererConfig.Builder<T> builder, EntityRendererProvider.Context context) {
        super(builder.build(), context);
    }

    protected static <T extends AbstractHuman> AzEntityRendererConfig.Builder<T> createConfig() {
        return AzEntityRendererConfig.<T>builder(AbstractHumanRenderer::getModel, AbstractHumanRenderer::getTexture)
            .addRenderLayer(new HumanArmorLayer<>())
            .addRenderLayer(new HumanHairLayer<>())
            .addRenderLayer(new HumanEyesLayer<>())
            .addRenderLayer(new HumanSkinLayer<>())
            .addRenderLayer(new HumanBeardLayer<>())
            .addRenderLayer(new HumanItemLayer<>());
    }
}

package com.alien.client.render.entity;

import com.alien.client.animation.entity.WarriorAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.RadiationGlowLayer;

public class WarriorRenderer extends AzEntityRenderer<Warrior> {

    private static final String NAME = "warrior";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public WarriorRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, WarriorRenderer::textureLocation)
                .setAnimatorProvider(WarriorAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Warrior warrior) {
        if (warrior.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (warrior.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (warrior.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}

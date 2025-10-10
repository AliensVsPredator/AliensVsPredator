package com.alien.client.render.entity;

import com.alien.client.animation.entity.WarriorAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class WarriorRenderer extends AzEntityRenderer<Warrior> {

    private static final String NAME = "warrior";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public WarriorRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(WarriorRenderer::modelLocation, WarriorRenderer::textureLocation)
                .setRenderType(WarriorRenderer::renderType)
                .setAnimatorProvider(WarriorAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Warrior warrior) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Warrior warrior) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(warrior.getVariant());
    }

    private static ResourceLocation textureLocation(Warrior warrior) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(warrior.getVariant());
    }
}

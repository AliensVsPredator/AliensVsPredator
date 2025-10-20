package com.alien.client.render.entity;

import com.alien.common.gameplay.entity.living.alien.ovipositor.Ovipositor;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class OvipositorRenderer extends AzEntityRenderer<Ovipositor> {

    private static final String NAME = "ovipositor";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public OvipositorRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<Ovipositor>builder(MODEL, TEXTURE).build(), context);
        this.shadowRadius = 0.4F;
    }
}

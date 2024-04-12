package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.SpitterModel;
import org.avp.common.entity.living.Spitter;

public class SpitterRenderer extends GeoEntityRenderer<Spitter> {

    public SpitterRenderer(EntityRendererProvider.Context context) {
        super(context, new SpitterModel());
    }
}

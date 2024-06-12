package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.base_line.SpitterModel;
import org.avp.client.render.entity.living.alien.base_line.layer.SpitterGlowLayer;
import org.avp.common.game.entity.living.alien.base_line.Spitter;

public class SpitterRenderer extends GeoEntityRenderer<Spitter> {

    public SpitterRenderer(EntityRendererProvider.Context context) {
        super(context, new SpitterModel());
        this.addRenderLayer(new SpitterGlowLayer(this));
    }
}

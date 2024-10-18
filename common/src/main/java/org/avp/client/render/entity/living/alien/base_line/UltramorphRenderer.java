package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.base_line.UltramorphModel;
import org.avp.common.game.entity.living.alien.base_line.Ultramorph;

public class UltramorphRenderer extends GeoEntityRenderer<Ultramorph> {

    public UltramorphRenderer(EntityRendererProvider.Context context) {
        super(context, new UltramorphModel());
    }
}

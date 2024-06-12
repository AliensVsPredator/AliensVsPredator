package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.base_line.PraetorianModel;
import org.avp.common.game.entity.living.alien.base_line.Praetorian;

public class PraetorianRenderer extends GeoEntityRenderer<Praetorian> {

    public PraetorianRenderer(EntityRendererProvider.Context context) {
        super(context, new PraetorianModel());
    }
}

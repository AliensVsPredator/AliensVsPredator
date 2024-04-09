package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.PraetorianModel;
import org.avp.common.entity.living.Praetorian;

public class PraetorianRenderer extends GeoEntityRenderer<Praetorian> {

    public PraetorianRenderer(EntityRendererProvider.Context context) {
        super(context, new PraetorianModel());
    }
}

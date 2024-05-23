package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.DracomorphModel;
import org.avp.common.entity.living.Dracomorph;

public class DracomorphRenderer extends GeoEntityRenderer<Dracomorph> {

    public DracomorphRenderer(EntityRendererProvider.Context context) {
        super(context, new DracomorphModel());
        this.withScale(1.75F);
    }
}

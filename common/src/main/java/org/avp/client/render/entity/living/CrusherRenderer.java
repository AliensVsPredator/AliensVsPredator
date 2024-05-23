package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.CrusherModel;
import org.avp.common.entity.living.Crusher;

public class CrusherRenderer extends GeoEntityRenderer<Crusher> {

    public CrusherRenderer(EntityRendererProvider.Context context) {
        super(context, new CrusherModel());
        this.withScale(1.25F);
    }
}

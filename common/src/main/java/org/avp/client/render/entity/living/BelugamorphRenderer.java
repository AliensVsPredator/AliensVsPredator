package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.BelugamorphModel;
import org.avp.common.entity.living.Belugamorph;

public class BelugamorphRenderer extends GeoEntityRenderer<Belugamorph> {

    public BelugamorphRenderer(EntityRendererProvider.Context context) {
        super(context, new BelugamorphModel());
    }
}

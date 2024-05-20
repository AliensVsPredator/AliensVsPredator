package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.entity.living.Belugaburster;

public class BelugabursterRenderer extends GeoEntityRenderer<Belugaburster> {

    public BelugabursterRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("belugaburster", GeoModelType.ENTITY));
    }
}

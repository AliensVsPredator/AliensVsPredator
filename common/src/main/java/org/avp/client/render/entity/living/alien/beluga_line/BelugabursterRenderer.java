package org.avp.client.render.entity.living.alien.beluga_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.beluga_line.Belugaburster;

public class BelugabursterRenderer extends GeoEntityRenderer<Belugaburster> {

    public BelugabursterRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("belugaburster", GeoModelType.ENTITY));
    }
}

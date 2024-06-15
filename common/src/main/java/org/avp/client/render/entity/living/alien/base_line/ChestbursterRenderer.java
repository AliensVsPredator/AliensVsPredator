package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.base_line.Chestburster;

public class ChestbursterRenderer extends GeoEntityRenderer<Chestburster> {

    public ChestbursterRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("chestburster", GeoModelType.ENTITY));
        withScale(0.65F);
    }
}

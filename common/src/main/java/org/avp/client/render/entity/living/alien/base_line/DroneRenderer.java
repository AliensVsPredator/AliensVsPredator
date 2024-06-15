package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.base_line.DroneModel;
import org.avp.common.game.entity.living.alien.base_line.Drone;

public class DroneRenderer extends GeoEntityRenderer<Drone> {

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneModel());
    }
}

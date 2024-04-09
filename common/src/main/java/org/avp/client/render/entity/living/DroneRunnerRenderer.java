package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.DroneRunnerModel;
import org.avp.common.entity.living.DroneRunner;

public class DroneRunnerRenderer extends GeoEntityRenderer<DroneRunner> {

    public DroneRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneRunnerModel());
    }
}

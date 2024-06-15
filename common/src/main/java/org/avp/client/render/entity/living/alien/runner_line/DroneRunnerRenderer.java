package org.avp.client.render.entity.living.alien.runner_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.runner_line.DroneRunnerModel;
import org.avp.common.game.entity.living.alien.runner_line.DroneRunner;

public class DroneRunnerRenderer extends GeoEntityRenderer<DroneRunner> {

    public DroneRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneRunnerModel());
    }
}

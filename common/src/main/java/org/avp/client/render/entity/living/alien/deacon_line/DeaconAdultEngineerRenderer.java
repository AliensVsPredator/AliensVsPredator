package org.avp.client.render.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.deacon_line.DeaconAdultEngineerModel;
import org.avp.common.game.entity.living.alien.deacon_line.DeaconAdultEngineer;

public class DeaconAdultEngineerRenderer extends GeoEntityRenderer<DeaconAdultEngineer> {

    public DeaconAdultEngineerRenderer(EntityRendererProvider.Context context) {
        super(context, new DeaconAdultEngineerModel());
        this.withScale(0.75F);
    }
}

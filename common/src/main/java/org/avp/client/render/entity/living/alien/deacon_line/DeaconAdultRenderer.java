package org.avp.client.render.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.deacon_line.DeaconAdultModel;
import org.avp.common.game.entity.living.alien.deacon_line.DeaconAdult;

public class DeaconAdultRenderer extends GeoEntityRenderer<DeaconAdult> {

    public DeaconAdultRenderer(EntityRendererProvider.Context context) {
        super(context, new DeaconAdultModel());
        this.withScale(0.75F);
    }
}

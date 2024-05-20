package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.DeaconAdultModel;
import org.avp.common.entity.living.DeaconAdult;

public class DeaconAdultRenderer extends GeoEntityRenderer<DeaconAdult> {

    public DeaconAdultRenderer(EntityRendererProvider.Context context) {
        super(context, new DeaconAdultModel());
        this.withScale(0.75F);
    }
}

package org.avp.client.render.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.deacon_line.DeaconModel;
import org.avp.common.game.entity.living.alien.deacon_line.Deacon;

public class DeaconRenderer extends GeoEntityRenderer<Deacon> {

    public DeaconRenderer(EntityRendererProvider.Context context) {
        super(context, new DeaconModel());
        this.withScale(0.75F);
    }
}

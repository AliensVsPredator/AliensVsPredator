package org.avp.client.render.entity.living.alien.draco_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.draco_line.OvamorphDraco;

public class OvamorphDracoRenderer extends GeoEntityRenderer<OvamorphDraco> {

    public OvamorphDracoRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("ovamorph_draco", GeoModelType.ENTITY));
        this.withScale(1.75F);
    }
}

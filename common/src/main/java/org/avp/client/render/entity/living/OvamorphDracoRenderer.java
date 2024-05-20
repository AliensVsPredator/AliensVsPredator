package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.entity.living.OvamorphDraco;

public class OvamorphDracoRenderer extends GeoEntityRenderer<OvamorphDraco> {

    public OvamorphDracoRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("ovamorph_draco", GeoModelType.ENTITY));
        this.withScale(1.75F);
    }
}

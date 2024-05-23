package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.entity.living.Trilobite;

public class TrilobiteRenderer extends GeoEntityRenderer<Trilobite> {

    public TrilobiteRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("trilobite", GeoModelType.ENTITY));
        this.withScale(1.75F);
    }
}

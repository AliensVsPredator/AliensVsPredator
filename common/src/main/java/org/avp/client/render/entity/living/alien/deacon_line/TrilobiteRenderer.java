package org.avp.client.render.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.deacon_line.Trilobite;

public class TrilobiteRenderer extends GeoEntityRenderer<Trilobite> {

    public TrilobiteRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("trilobite", GeoModelType.ENTITY));
        this.withScale(1.75F);
    }
}

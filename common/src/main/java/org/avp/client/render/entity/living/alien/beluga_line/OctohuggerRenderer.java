package org.avp.client.render.entity.living.alien.beluga_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.beluga_line.Octohugger;

public class OctohuggerRenderer extends GeoEntityRenderer<Octohugger> {

    public OctohuggerRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("octohugger", GeoModelType.ENTITY));
    }
}

package org.avp.client.render.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.deacon_line.TrilobiteBaby;

public class TrilobiteBabyRenderer extends GeoEntityRenderer<TrilobiteBaby> {

    public TrilobiteBabyRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("trilobite_baby", GeoModelType.ENTITY));
    }
}

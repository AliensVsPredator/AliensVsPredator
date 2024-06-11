package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.entity.living.alien.base_line.FacehuggerRoyal;

public class FacehuggerRoyalRenderer extends GeoEntityRenderer<FacehuggerRoyal> {

    public FacehuggerRoyalRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("facehugger_royal", GeoModelType.ENTITY));
    }
}

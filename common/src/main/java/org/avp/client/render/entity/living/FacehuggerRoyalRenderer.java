package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.entity.living.FacehuggerRoyal;

public class FacehuggerRoyalRenderer extends GeoEntityRenderer<FacehuggerRoyal> {

    public FacehuggerRoyalRenderer(EntityRendererProvider.Context context) {
        super(context, new AVPGeoModel<>("facehugger_royal", GeoModelType.ENTITY));
    }
}

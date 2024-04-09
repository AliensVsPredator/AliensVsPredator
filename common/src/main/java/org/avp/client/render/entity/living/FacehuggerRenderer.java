package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.FacehuggerModel;
import org.avp.common.entity.living.Facehugger;

public class FacehuggerRenderer extends GeoEntityRenderer<Facehugger> {

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(context, new FacehuggerModel());
    }
}

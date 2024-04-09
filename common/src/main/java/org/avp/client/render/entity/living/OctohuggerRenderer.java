package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.OctohuggerModel;
import org.avp.common.entity.living.Octohugger;

public class OctohuggerRenderer extends GeoEntityRenderer<Octohugger> {

    public OctohuggerRenderer(EntityRendererProvider.Context context) {
        super(context, new OctohuggerModel());
    }
}

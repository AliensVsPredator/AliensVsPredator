package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.DracobursterModel;
import org.avp.common.entity.living.Dracoburster;

public class DracobursterRenderer extends GeoEntityRenderer<Dracoburster> {

    public DracobursterRenderer(EntityRendererProvider.Context context) {
        super(context, new DracobursterModel());
    }
}

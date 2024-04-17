package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.ChestbursterDracoModel;
import org.avp.common.entity.living.ChestbursterDraco;

public class ChestbursterDracoRenderer extends GeoEntityRenderer<ChestbursterDraco> {

    public ChestbursterDracoRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterDracoModel());
    }
}

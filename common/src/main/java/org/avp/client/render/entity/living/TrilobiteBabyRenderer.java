package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.TrilobiteBabyModel;
import org.avp.common.entity.living.TrilobiteBaby;

public class TrilobiteBabyRenderer extends GeoEntityRenderer<TrilobiteBaby> {

    public TrilobiteBabyRenderer(EntityRendererProvider.Context context) {
        super(context, new TrilobiteBabyModel());
    }
}

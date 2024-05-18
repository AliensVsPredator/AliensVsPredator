package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.NauticomorphModel;
import org.avp.common.entity.living.Nauticomorph;

public class NauticomorphRenderer extends GeoEntityRenderer<Nauticomorph> {

    public NauticomorphRenderer(EntityRendererProvider.Context context) {
        super(context, new NauticomorphModel());
    }
}

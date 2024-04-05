package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.BoilerModel;
import org.avp.common.entity.living.Boiler;

/**
 * @author Boston Vanseghi
 */
public class BoilerRenderer extends GeoEntityRenderer<Boiler> {

    public BoilerRenderer(EntityRendererProvider.Context context) {
        super(context, new BoilerModel());
    }
}

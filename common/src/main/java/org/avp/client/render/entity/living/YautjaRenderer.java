package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.YautjaModel;
import org.avp.common.entity.living.Yautja;

/**
 * @author Boston Vanseghi
 */
public class YautjaRenderer extends GeoEntityRenderer<Yautja> {

    public YautjaRenderer(EntityRendererProvider.Context context) {
        super(context, new YautjaModel());
    }
}

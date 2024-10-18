package org.avp.client.render.entity.living.engineer;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.engineer.EngineerModel;
import org.avp.client.render.entity.living.engineer.layer.EngineerSuitLayer;
import org.avp.common.game.entity.living.engineer.Engineer;

public class EngineerRenderer extends GeoEntityRenderer<Engineer> {

    public EngineerRenderer(EntityRendererProvider.Context context) {
        super(context, new EngineerModel());
        this.addRenderLayer(new EngineerSuitLayer(this));
    }
}

package org.avp.client.render.entity.living.alien.runner_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.alien.base_line.CrusherModel;
import org.avp.common.game.entity.living.alien.runner_line.Crusher;

public class CrusherRenderer extends GeoEntityRenderer<Crusher> {

    public CrusherRenderer(EntityRendererProvider.Context context) {
        super(context, new CrusherModel());
        this.withScale(1.25F);
    }
}

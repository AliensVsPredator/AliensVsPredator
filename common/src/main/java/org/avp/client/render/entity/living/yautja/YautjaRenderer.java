package org.avp.client.render.entity.living.yautja;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.yautja.YautjaModel;
import org.avp.common.game.entity.living.yautja.Yautja;

public class YautjaRenderer extends GeoEntityRenderer<Yautja> {

    public YautjaRenderer(EntityRendererProvider.Context context) {
        super(context, new YautjaModel());
    }
}

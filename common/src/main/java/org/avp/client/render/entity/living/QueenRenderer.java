package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.QueenModel;
import org.avp.common.entity.living.Queen;

public class QueenRenderer extends GeoEntityRenderer<Queen> {

    public QueenRenderer(EntityRendererProvider.Context context) {
        super(context, new QueenModel());
        this.withScale(1.75F);
    }
}

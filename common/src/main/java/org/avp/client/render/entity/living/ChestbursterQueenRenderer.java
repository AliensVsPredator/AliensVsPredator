package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.ChestbursterQueenModel;
import org.avp.common.entity.living.ChestbursterQueen;

public class ChestbursterQueenRenderer extends GeoEntityRenderer<ChestbursterQueen> {

    public ChestbursterQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterQueenModel());
        this.withScale(0.5F);
    }
}

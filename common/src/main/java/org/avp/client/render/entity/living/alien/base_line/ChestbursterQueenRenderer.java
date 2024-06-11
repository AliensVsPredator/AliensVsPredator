package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.base_line.ChestbursterQueenModel;
import org.avp.common.game.entity.living.alien.base_line.ChestbursterQueen;

public class ChestbursterQueenRenderer extends GeoEntityRenderer<ChestbursterQueen> {

    public ChestbursterQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterQueenModel());
        this.withScale(0.5F);
    }
}

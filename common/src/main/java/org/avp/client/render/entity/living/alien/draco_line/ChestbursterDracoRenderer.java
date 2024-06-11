package org.avp.client.render.entity.living.alien.draco_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.draco_line.ChestbursterDracoModel;
import org.avp.common.game.entity.living.alien.draco_line.ChestbursterDraco;

public class ChestbursterDracoRenderer extends GeoEntityRenderer<ChestbursterDraco> {

    public ChestbursterDracoRenderer(EntityRendererProvider.Context context) {
        super(context, new ChestbursterDracoModel());
        this.withScale(0.5F);
    }
}

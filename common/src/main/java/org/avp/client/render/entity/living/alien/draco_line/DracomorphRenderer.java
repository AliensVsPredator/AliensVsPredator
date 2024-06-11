package org.avp.client.render.entity.living.alien.draco_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.draco_line.DracomorphModel;
import org.avp.common.game.entity.living.alien.draco_line.Dracomorph;

public class DracomorphRenderer extends GeoEntityRenderer<Dracomorph> {

    public DracomorphRenderer(EntityRendererProvider.Context context) {
        super(context, new DracomorphModel());
        this.withScale(1.75F);
    }
}

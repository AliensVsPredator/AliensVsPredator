package org.avp.client.render.entity.living.alien.beluga_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.beluga_line.BelugamorphModel;
import org.avp.common.game.entity.living.alien.beluga_line.Belugamorph;

public class BelugamorphRenderer extends GeoEntityRenderer<Belugamorph> {

    public BelugamorphRenderer(EntityRendererProvider.Context context) {
        super(context, new BelugamorphModel());
    }
}

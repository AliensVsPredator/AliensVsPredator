package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.base_line.WarriorModel;
import org.avp.common.game.entity.living.alien.base_line.Warrior;

public class WarriorRenderer extends GeoEntityRenderer<Warrior> {

    public WarriorRenderer(EntityRendererProvider.Context context) {
        super(context, new WarriorModel());
    }
}

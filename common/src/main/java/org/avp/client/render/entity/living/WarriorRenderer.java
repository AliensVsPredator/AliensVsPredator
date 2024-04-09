package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.WarriorModel;
import org.avp.common.entity.living.Warrior;

public class WarriorRenderer extends GeoEntityRenderer<Warrior> {

    public WarriorRenderer(EntityRendererProvider.Context context) {
        super(context, new WarriorModel());
    }
}

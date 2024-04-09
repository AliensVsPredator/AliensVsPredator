package org.avp.client.render.entity.living;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.avp.client.model.entity.living.WarriorRunnerModel;
import org.avp.common.entity.living.WarriorRunner;

public class WarriorRunnerRenderer extends GeoEntityRenderer<WarriorRunner> {

    public WarriorRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new WarriorRunnerModel());
    }
}

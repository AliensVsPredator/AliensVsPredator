package org.avp.client.render.entity.living.alien.runner_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.avp.client.model.entity.living.alien.runner_line.WarriorRunnerModel;
import org.avp.common.game.entity.living.alien.runner_line.WarriorRunner;

public class WarriorRunnerRenderer extends GeoEntityRenderer<WarriorRunner> {

    public WarriorRunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new WarriorRunnerModel());
    }
}

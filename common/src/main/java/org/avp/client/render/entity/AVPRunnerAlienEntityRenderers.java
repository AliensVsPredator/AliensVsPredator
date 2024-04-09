package org.avp.client.render.entity;

import org.avp.client.render.entity.living.ChestbursterRunnerRenderer;
import org.avp.client.render.entity.living.CrusherRenderer;
import org.avp.client.render.entity.living.DroneRunnerRenderer;
import org.avp.client.render.entity.living.WarriorRunnerRenderer;
import org.avp.common.entity.AVPRunnerAlienEntityTypes;

public class AVPRunnerAlienEntityRenderers {

    private AVPRunnerAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.CHESTBURSTER_RUNNER, ChestbursterRunnerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.CRUSHER, CrusherRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.DRONE_RUNNER, DroneRunnerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.WARRIOR_RUNNER, WarriorRunnerRenderer::new);
    }
}

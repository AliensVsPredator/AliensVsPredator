package org.avp.client.render.entity;

import org.avp.client.render.entity.living.ChestbursterRunnerRenderer;
import org.avp.client.render.entity.living.CrusherRenderer;
import org.avp.client.render.entity.living.DroneRunnerRenderer;
import org.avp.client.render.entity.living.WarriorRunnerRenderer;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;

public class AVPRunnerAlienEntityRenderers {

    private AVPRunnerAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner, ChestbursterRunnerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.crusher, CrusherRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner, DroneRunnerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner, WarriorRunnerRenderer::new);
    }
}

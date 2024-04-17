package org.avp.client.render.entity;

import org.avp.client.render.entity.living.*;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;

public class AVPExoticAlienEntityRenderers {

    private AVPExoticAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer, DeaconAdultEngineerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco, ChestbursterDracoRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.dracomorph, DracomorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.octohugger, OctohuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.ovamorphDraco, OvamorphDracoRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.ultramorph, UltramorphRenderer::new);
    }
}

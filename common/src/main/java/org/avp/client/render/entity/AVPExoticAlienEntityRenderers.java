package org.avp.client.render.entity;

import org.avp.client.render.entity.living.*;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;

public class AVPExoticAlienEntityRenderers {

    private AVPExoticAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.DEACON_ADULT_ENGINEER, DeaconAdultEngineerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.DRACOBURSTER, DracobursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.DRACOMORPH, DracomorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.OCTOHUGGER, OctohuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.OVAMORPH_DRACO, OvamorphDracoRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.ULTRAMORPH, UltramorphRenderer::new);
    }
}

package org.avp.client.render.entity;

import org.avp.client.render.entity.living.*;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;

public class AVPExoticAlienEntityRenderers {

    private AVPExoticAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.DEACON_ADULT_ENGINEER, DeaconAdultEngineerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.DRACOBURSTER, DracobursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.DRACOMORPH, DracomorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.OCTOHUGGER, OctohuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.OVAMORPH_DRACO, OvamorphDracoRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPExoticAlienEntityTypes.ULTRAMORPH, UltramorphRenderer::new);
    }
}

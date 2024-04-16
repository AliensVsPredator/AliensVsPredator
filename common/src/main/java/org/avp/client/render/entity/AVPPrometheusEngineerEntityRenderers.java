package org.avp.client.render.entity;

import org.avp.client.render.entity.living.EngineerRenderer;
import org.avp.common.entity.type.AVPEngineerEntityTypes;

public class AVPPrometheusEngineerEntityRenderers {

    private AVPPrometheusEngineerEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPEngineerEntityTypes.ENGINEER, EngineerRenderer::new);
    }
}

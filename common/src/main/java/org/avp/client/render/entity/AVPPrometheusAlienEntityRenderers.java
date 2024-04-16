package org.avp.client.render.entity;

import org.avp.client.render.entity.living.DeaconAdultRenderer;
import org.avp.client.render.entity.living.DeaconRenderer;
import org.avp.client.render.entity.living.TrilobiteBabyRenderer;
import org.avp.client.render.entity.living.TrilobiteRenderer;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;

public class AVPPrometheusAlienEntityRenderers {

    private AVPPrometheusAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.DEACON, DeaconRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.DEACON_ADULT, DeaconAdultRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.TRILOBITE, TrilobiteRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.TRILOBITE_BABY, TrilobiteBabyRenderer::new);
    }
}

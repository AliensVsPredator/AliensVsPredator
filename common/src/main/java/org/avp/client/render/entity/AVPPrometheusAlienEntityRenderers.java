package org.avp.client.render.entity;

import org.avp.client.render.entity.living.DeaconAdultRenderer;
import org.avp.client.render.entity.living.DeaconRenderer;
import org.avp.client.render.entity.living.TrilobiteBabyRenderer;
import org.avp.client.render.entity.living.TrilobiteRenderer;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;

public class AVPPrometheusAlienEntityRenderers {

    private AVPPrometheusAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.DEACON, DeaconRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.DEACON_ADULT, DeaconAdultRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.TRILOBITE, TrilobiteRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.TRILOBITE_BABY, TrilobiteBabyRenderer::new);
    }
}

package org.avp.client.render.entity;

import org.avp.client.render.entity.living.BelugabursterRenderer;
import org.avp.client.render.entity.living.BelugamorphRenderer;
import org.avp.common.entity.type.AVPEntityTypes;

public class AVPEntityRenderers {

    private AVPEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.acid, AcidRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.belugaburster, BelugabursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.belugamorph, BelugamorphRenderer::new);
    }
}

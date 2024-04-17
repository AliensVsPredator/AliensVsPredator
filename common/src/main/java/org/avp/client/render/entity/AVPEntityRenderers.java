package org.avp.client.render.entity;

import org.avp.client.render.entity.living.BelugabursterRenderer;
import org.avp.client.render.entity.living.BelugamorphRenderer;
import org.avp.common.entity.type.AVPEntityTypes;

public class AVPEntityRenderers {

    private AVPEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.ACID, AcidRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.BELUGABURSTER, BelugabursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPEntityTypes.INSTANCE.BELUGAMORPH, BelugamorphRenderer::new);
    }
}

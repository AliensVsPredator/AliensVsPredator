package org.avp.client.render.entity;

import org.avp.client.render.entity.living.YautjaRenderer;
import org.avp.common.entity.type.AVPYautjaEntityTypes;

public class AVPYautjaEntityRenderers {

    private AVPYautjaEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPYautjaEntityTypes.INSTANCE.yautja, YautjaRenderer::new);
    }
}

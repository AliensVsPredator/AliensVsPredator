package org.avp.client.render.entity;

import org.avp.client.render.entity.living.BoilerRenderer;
import org.avp.client.render.entity.living.ChestbursterRenderer;
import org.avp.client.render.entity.living.DroneRenderer;
import org.avp.client.render.entity.living.FacehuggerRenderer;
import org.avp.client.render.entity.living.FacehuggerRoyalRenderer;
import org.avp.client.render.entity.living.OvamorphRenderer;
import org.avp.client.render.entity.living.PraetorianRenderer;
import org.avp.client.render.entity.living.QueenRenderer;
import org.avp.client.render.entity.living.SpitterRenderer;
import org.avp.client.render.entity.living.WarriorRenderer;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;

public class AVPBaseAlienEntityRenderers {

    private AVPBaseAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.BOILER, BoilerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.CHESTBURSTER, ChestbursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.DRONE, DroneRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.FACEHUGGER, FacehuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.FACEHUGGER_ROYAL, FacehuggerRoyalRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.OVAMORPH, OvamorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.PRAETORIAN, PraetorianRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.QUEEN, QueenRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.SPITTER, SpitterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.WARRIOR, WarriorRenderer::new);
    }
}

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
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.BOILER, BoilerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.DRONE, DroneRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.FACEHUGGER_ROYAL, FacehuggerRoyalRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.OVAMORPH, OvamorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.QUEEN, QueenRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.SPITTER, SpitterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.WARRIOR, WarriorRenderer::new);
    }
}

package org.avp.client.render.entity;

import org.avp.client.render.entity.living.BoilerRenderer;
import org.avp.client.render.entity.living.ChestbursterRenderer;
import org.avp.client.render.entity.living.DroneRenderer;
import org.avp.client.render.entity.living.FacehuggerRenderer;
import org.avp.client.render.entity.living.FacehuggerRoyalRenderer;
import org.avp.client.render.entity.living.NauticomorphRenderer;
import org.avp.client.render.entity.living.OvamorphRenderer;
import org.avp.client.render.entity.living.PraetorianRenderer;
import org.avp.client.render.entity.living.QueenRenderer;
import org.avp.client.render.entity.living.SpitterRenderer;
import org.avp.client.render.entity.living.WarriorRenderer;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;

public class AVPBaseAlienEntityRenderers {

    private AVPBaseAlienEntityRenderers() {}

    public static void addBindings() {
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.boiler, BoilerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.chestburster, ChestbursterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.drone, DroneRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehugger, FacehuggerRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal, FacehuggerRoyalRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.nauticomorph, NauticomorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.ovamorph, OvamorphRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.praetorian, PraetorianRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.queen, QueenRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.spitter, SpitterRenderer::new);
        AVPEntityRenderRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.warrior, WarriorRenderer::new);
    }
}

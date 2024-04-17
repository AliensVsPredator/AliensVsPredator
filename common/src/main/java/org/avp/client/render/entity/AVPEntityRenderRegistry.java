package org.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.Holder;
import org.avp.client.util.EntityRenderData;

public class AVPEntityRenderRegistry {

    private static final List<EntityRenderData<? extends Entity>> BINDINGS = new ArrayList<>();

    public static List<EntityRenderData<? extends Entity>> getBindings() {
        return BINDINGS;
    }

    protected static <T extends Entity> void addBinding(
        Holder<EntityType<T>> entityTypeHolder,
        EntityRendererProvider<T> entityRendererProvider
    ) {
        BINDINGS.add(new EntityRenderData<>(entityTypeHolder, entityRendererProvider));
    }

    static {
        AVPBaseAlienEntityRenderers.addBindings();
        AVPEntityRenderers.addBindings();
        AVPExoticAlienEntityRenderers.addBindings();
        AVPPrometheusAlienEntityRenderers.addBindings();
        AVPPrometheusEngineerEntityRenderers.addBindings();
        AVPRunnerAlienEntityRenderers.addBindings();
        AVPYautjaEntityRenderers.addBindings();
    }

    private AVPEntityRenderRegistry() {
        throw new UnsupportedOperationException();
    }
}

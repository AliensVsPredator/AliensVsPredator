package org.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
import org.avp.client.util.EntityRenderData;

/**
 * @author Boston Vanseghi
 */
public class AVPEntityRenderRegistry {

    private static final List<EntityRenderData<? extends Entity>> BINDINGS = new ArrayList<>();

    public static List<EntityRenderData<? extends Entity>> getBindings() {
        return BINDINGS;
    }

    protected static <T extends Mob> void addBinding(
        GameObject<EntityType<T>> entityTypeGameObject,
        EntityRendererProvider<T> entityRendererProvider
    ) {
        BINDINGS.add(new EntityRenderData<>(entityTypeGameObject, entityRendererProvider));
    }

    static {
        AVPBaseAlienEntityRenderers.addBindings();
        AVPEntityRenderers.addBindings();
        AVPExoticAlienEntityRenderers.addBindings();
        AVPPrometheusAlienEntityRenderers.addBindings();
        AVPPrometheusEngineerEntityRenderers.addBindings();
        AVPRunnerAlienEntityRenderers.addBindings();
    }

    private AVPEntityRenderRegistry() {
        throw new UnsupportedOperationException();
    }
}

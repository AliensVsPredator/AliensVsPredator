package org.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.Holder;
import org.avp.client.render.entity.living.BelugabursterRenderer;
import org.avp.client.render.entity.living.BelugamorphRenderer;
import org.avp.client.render.entity.living.BoilerRenderer;
import org.avp.client.render.entity.living.ChestbursterDracoRenderer;
import org.avp.client.render.entity.living.ChestbursterQueenRenderer;
import org.avp.client.render.entity.living.ChestbursterRenderer;
import org.avp.client.render.entity.living.ChestbursterRunnerRenderer;
import org.avp.client.render.entity.living.CrusherRenderer;
import org.avp.client.render.entity.living.DeaconAdultEngineerRenderer;
import org.avp.client.render.entity.living.DeaconAdultRenderer;
import org.avp.client.render.entity.living.DeaconRenderer;
import org.avp.client.render.entity.living.DracomorphRenderer;
import org.avp.client.render.entity.living.DroneRenderer;
import org.avp.client.render.entity.living.DroneRunnerRenderer;
import org.avp.client.render.entity.living.EngineerRenderer;
import org.avp.client.render.entity.living.FacehuggerRenderer;
import org.avp.client.render.entity.living.FacehuggerRoyalRenderer;
import org.avp.client.render.entity.living.NauticomorphRenderer;
import org.avp.client.render.entity.living.OctohuggerRenderer;
import org.avp.client.render.entity.living.OvamorphDracoRenderer;
import org.avp.client.render.entity.living.OvamorphRenderer;
import org.avp.client.render.entity.living.PraetorianRenderer;
import org.avp.client.render.entity.living.QueenRenderer;
import org.avp.client.render.entity.living.SpitterRenderer;
import org.avp.client.render.entity.living.TrilobiteBabyRenderer;
import org.avp.client.render.entity.living.TrilobiteRenderer;
import org.avp.client.render.entity.living.UltramorphRenderer;
import org.avp.client.render.entity.living.WarriorRenderer;
import org.avp.client.render.entity.living.WarriorRunnerRenderer;
import org.avp.client.render.entity.living.YautjaRenderer;
import org.avp.client.util.EntityRenderData;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPEngineerEntityTypes;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.type.AVPYautjaEntityTypes;

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
        // Misc. entities
        addBinding(AVPEntityTypes.INSTANCE.acid, AcidRenderer::new);
        addBinding(AVPEntityTypes.INSTANCE.belugaburster, BelugabursterRenderer::new);
        addBinding(AVPEntityTypes.INSTANCE.belugamorph, BelugamorphRenderer::new);

        // Base aliens
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.boiler, BoilerRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.chestburster, ChestbursterRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.chestbursterQueen, ChestbursterQueenRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.drone, DroneRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehugger, FacehuggerRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal, FacehuggerRoyalRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.nauticomorph, NauticomorphRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.ovamorph, OvamorphRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.praetorian, PraetorianRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.queen, QueenRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.spitter, SpitterRenderer::new);
        addBinding(AVPBaseAlienEntityTypes.INSTANCE.warrior, WarriorRenderer::new);

        // Exotic aliens
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer, DeaconAdultEngineerRenderer::new);
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco, ChestbursterDracoRenderer::new);
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.dracomorph, DracomorphRenderer::new);
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.octohugger, OctohuggerRenderer::new);
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.ovamorphDraco, OvamorphDracoRenderer::new);
        addBinding(AVPExoticAlienEntityTypes.INSTANCE.ultramorph, UltramorphRenderer::new);

        // Prometheus aliens
        addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.deacon, DeaconRenderer::new);
        addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult, DeaconAdultRenderer::new);
        addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.trilobite, TrilobiteRenderer::new);
        addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.trilobiteBaby, TrilobiteBabyRenderer::new);

        // Prometheus engineers
        addBinding(AVPEngineerEntityTypes.INSTANCE.engineer, EngineerRenderer::new);

        // Runner aliens
        addBinding(AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner, ChestbursterRunnerRenderer::new);
        addBinding(AVPRunnerAlienEntityTypes.INSTANCE.crusher, CrusherRenderer::new);
        addBinding(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner, DroneRunnerRenderer::new);
        addBinding(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner, WarriorRunnerRenderer::new);

        // Yautja
        addBinding(AVPYautjaEntityTypes.INSTANCE.yautja, YautjaRenderer::new);
    }

    private AVPEntityRenderRegistry() {
        throw new UnsupportedOperationException();
    }
}

package org.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

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
import org.avp.common.registry.AVPDeferredRegistry;

public class AVPEntityRenderRegistry extends AVPDeferredRegistry<EntityRenderData<? extends Entity>> {

    public static final AVPEntityRenderRegistry INSTANCE = new AVPEntityRenderRegistry();

    protected <T extends Entity> Holder<EntityRenderData<? extends Entity>> createHolder(
        Holder<EntityType<T>> entityTypeHolder,
        EntityRendererProvider<T> entityRendererProvider
    ) {
        return createHolder(entityTypeHolder.getResourceLocation().getPath(), () -> new EntityRenderData<>(entityTypeHolder, entityRendererProvider));
    }

    @Override
    protected Holder<EntityRenderData<?>> createHolder(String registryName, Supplier<EntityRenderData<?>> supplier) {
        var holder = new Holder<>(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    private AVPEntityRenderRegistry() {
        // Misc. entities
        createHolder(AVPEntityTypes.INSTANCE.acid, AcidRenderer::new);
        createHolder(AVPEntityTypes.INSTANCE.belugaburster, BelugabursterRenderer::new);
        createHolder(AVPEntityTypes.INSTANCE.belugamorph, BelugamorphRenderer::new);

        // Base aliens
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.boiler, BoilerRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.chestburster, ChestbursterRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.chestbursterQueen, ChestbursterQueenRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.drone, DroneRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.facehugger, FacehuggerRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal, FacehuggerRoyalRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.nauticomorph, NauticomorphRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.ovamorph, OvamorphRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.praetorian, PraetorianRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.queen, QueenRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.spitter, SpitterRenderer::new);
        createHolder(AVPBaseAlienEntityTypes.INSTANCE.warrior, WarriorRenderer::new);

        // Exotic aliens
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer, DeaconAdultEngineerRenderer::new);
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco, ChestbursterDracoRenderer::new);
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.dracomorph, DracomorphRenderer::new);
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.octohugger, OctohuggerRenderer::new);
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.ovamorphDraco, OvamorphDracoRenderer::new);
        createHolder(AVPExoticAlienEntityTypes.INSTANCE.ultramorph, UltramorphRenderer::new);

        // Prometheus aliens
        createHolder(AVPPrometheusAlienEntityTypes.INSTANCE.deacon, DeaconRenderer::new);
        createHolder(AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult, DeaconAdultRenderer::new);
        createHolder(AVPPrometheusAlienEntityTypes.INSTANCE.trilobite, TrilobiteRenderer::new);
        createHolder(AVPPrometheusAlienEntityTypes.INSTANCE.trilobiteBaby, TrilobiteBabyRenderer::new);

        // Prometheus engineers
        createHolder(AVPEngineerEntityTypes.INSTANCE.engineer, EngineerRenderer::new);

        // Runner aliens
        createHolder(AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner, ChestbursterRunnerRenderer::new);
        createHolder(AVPRunnerAlienEntityTypes.INSTANCE.crusher, CrusherRenderer::new);
        createHolder(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner, DroneRunnerRenderer::new);
        createHolder(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner, WarriorRunnerRenderer::new);

        // Yautja
        createHolder(AVPYautjaEntityTypes.INSTANCE.yautja, YautjaRenderer::new);
    }

    @Override
    public void register() { /* Do nothing */ }
}

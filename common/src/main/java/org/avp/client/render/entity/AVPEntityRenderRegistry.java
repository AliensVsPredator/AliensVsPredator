package org.avp.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
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
import org.avp.common.entity.data.AcidData;
import org.avp.common.entity.data.BelugabursterData;
import org.avp.common.entity.data.BelugamorphData;
import org.avp.common.entity.data.BoilerData;
import org.avp.common.entity.data.ChestbursterData;
import org.avp.common.entity.data.ChestbursterDracoData;
import org.avp.common.entity.data.ChestbursterQueenData;
import org.avp.common.entity.data.ChestbursterRunnerData;
import org.avp.common.entity.data.CrusherData;
import org.avp.common.entity.data.DeaconAdultData;
import org.avp.common.entity.data.DeaconAdultEngineerData;
import org.avp.common.entity.data.DeaconData;
import org.avp.common.entity.data.DracomorphData;
import org.avp.common.entity.data.DroneData;
import org.avp.common.entity.data.DroneRunnerData;
import org.avp.common.entity.data.EngineerData;
import org.avp.common.entity.data.FacehuggerData;
import org.avp.common.entity.data.FacehuggerRoyalData;
import org.avp.common.entity.data.NauticomorphData;
import org.avp.common.entity.data.OctohuggerData;
import org.avp.common.entity.data.OvamorphData;
import org.avp.common.entity.data.OvamorphDracoData;
import org.avp.common.entity.data.PraetorianData;
import org.avp.common.entity.data.QueenData;
import org.avp.common.entity.data.SpitterData;
import org.avp.common.entity.data.TrilobiteBabyData;
import org.avp.common.entity.data.TrilobiteData;
import org.avp.common.entity.data.UltramorphData;
import org.avp.common.entity.data.WarriorData;
import org.avp.common.entity.data.WarriorRunnerData;
import org.avp.common.entity.data.YautjaData;
import org.avp.common.entity.data.AVPEntityDataRegistry;
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
        entries.put(registryName, holder);
        return holder;
    }

    private AVPEntityRenderRegistry() {
        // Misc. entities
        createHolder(AcidData.INSTANCE.getHolder(), AcidRenderer::new);
        createHolder(BelugabursterData.INSTANCE.getHolder(), BelugabursterRenderer::new);
        createHolder(BelugamorphData.INSTANCE.getHolder(), BelugamorphRenderer::new);

        // Base aliens
        createHolder(BoilerData.INSTANCE.getHolder(), BoilerRenderer::new);
        createHolder(ChestbursterData.INSTANCE.getHolder(), ChestbursterRenderer::new);
        createHolder(ChestbursterQueenData.INSTANCE.getHolder(), ChestbursterQueenRenderer::new);
        createHolder(DroneData.INSTANCE.getHolder(), DroneRenderer::new);
        createHolder(FacehuggerData.INSTANCE.getHolder(), FacehuggerRenderer::new);
        createHolder(FacehuggerRoyalData.INSTANCE.getHolder(), FacehuggerRoyalRenderer::new);
        createHolder(NauticomorphData.INSTANCE.getHolder(), NauticomorphRenderer::new);
        createHolder(OvamorphData.INSTANCE.getHolder(), OvamorphRenderer::new);
        createHolder(PraetorianData.INSTANCE.getHolder(), PraetorianRenderer::new);
        createHolder(QueenData.INSTANCE.getHolder(), QueenRenderer::new);
        createHolder(SpitterData.INSTANCE.getHolder(), SpitterRenderer::new);
        createHolder(WarriorData.INSTANCE.getHolder(), WarriorRenderer::new);

        // Exotic aliens
        createHolder(DeaconAdultEngineerData.INSTANCE.getHolder(), DeaconAdultEngineerRenderer::new);
        createHolder(ChestbursterDracoData.INSTANCE.getHolder(), ChestbursterDracoRenderer::new);
        createHolder(DracomorphData.INSTANCE.getHolder(), DracomorphRenderer::new);
        createHolder(OctohuggerData.INSTANCE.getHolder(), OctohuggerRenderer::new);
        createHolder(OvamorphDracoData.INSTANCE.getHolder(), OvamorphDracoRenderer::new);
        createHolder(UltramorphData.INSTANCE.getHolder(), UltramorphRenderer::new);

        // Prometheus aliens
        createHolder(DeaconData.INSTANCE.getHolder(), DeaconRenderer::new);
        createHolder(DeaconAdultData.INSTANCE.getHolder(), DeaconAdultRenderer::new);
        createHolder(TrilobiteData.INSTANCE.getHolder(), TrilobiteRenderer::new);
        createHolder(TrilobiteBabyData.INSTANCE.getHolder(), TrilobiteBabyRenderer::new);

        // Prometheus engineers
        createHolder(EngineerData.INSTANCE.getHolder(), EngineerRenderer::new);

        // Runner aliens
        createHolder(ChestbursterRunnerData.INSTANCE.getHolder(), ChestbursterRunnerRenderer::new);
        createHolder(CrusherData.INSTANCE.getHolder(), CrusherRenderer::new);
        createHolder(DroneRunnerData.INSTANCE.getHolder(), DroneRunnerRenderer::new);
        createHolder(WarriorRunnerData.INSTANCE.getHolder(), WarriorRunnerRenderer::new);

        // Yautja
        createHolder(YautjaData.INSTANCE.getHolder(), YautjaRenderer::new);
    }

    @Override
    public void register() { /* Do nothing */ }

    public void verifyAllRendererProvidersPresent() throws IllegalStateException {
        AVPEntityDataRegistry.INSTANCE.getEntries().forEach(entityData -> {
            var renderDataHolder = entries.get(entityData.getRegistryName());
            if (!Objects.equals(entityData.getHolder(), renderDataHolder.get().entityTypeHolder())) {
                throw new IllegalStateException("Entity was registered, but does not have a render provider holder! Entity: " + entityData.getHolder().getResourceLocation());
            }
        });
    }
}

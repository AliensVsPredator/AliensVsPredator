package org.avp.client.registry;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.function.Supplier;

import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.client.render.entity.AcidRenderer;
import org.avp.client.render.entity.RocketRenderer;
import org.avp.client.render.entity.living.alien.base_line.BoilerRenderer;
import org.avp.client.render.entity.living.alien.base_line.ChestbursterQueenRenderer;
import org.avp.client.render.entity.living.alien.base_line.ChestbursterRenderer;
import org.avp.client.render.entity.living.alien.base_line.DroneRenderer;
import org.avp.client.render.entity.living.alien.base_line.FacehuggerRenderer;
import org.avp.client.render.entity.living.alien.base_line.FacehuggerRoyalRenderer;
import org.avp.client.render.entity.living.alien.base_line.NauticomorphRenderer;
import org.avp.client.render.entity.living.alien.base_line.OvamorphRenderer;
import org.avp.client.render.entity.living.alien.base_line.PraetorianRenderer;
import org.avp.client.render.entity.living.alien.base_line.QueenRenderer;
import org.avp.client.render.entity.living.alien.base_line.SpitterRenderer;
import org.avp.client.render.entity.living.alien.base_line.UltramorphRenderer;
import org.avp.client.render.entity.living.alien.base_line.WarriorRenderer;
import org.avp.client.render.entity.living.alien.beluga_line.BelugabursterRenderer;
import org.avp.client.render.entity.living.alien.beluga_line.BelugamorphRenderer;
import org.avp.client.render.entity.living.alien.beluga_line.OctohuggerRenderer;
import org.avp.client.render.entity.living.alien.deacon_line.DeaconAdultEngineerRenderer;
import org.avp.client.render.entity.living.alien.deacon_line.DeaconAdultRenderer;
import org.avp.client.render.entity.living.alien.deacon_line.DeaconRenderer;
import org.avp.client.render.entity.living.alien.deacon_line.TrilobiteBabyRenderer;
import org.avp.client.render.entity.living.alien.deacon_line.TrilobiteRenderer;
import org.avp.client.render.entity.living.alien.draco_line.ChestbursterDracoRenderer;
import org.avp.client.render.entity.living.alien.draco_line.DracomorphRenderer;
import org.avp.client.render.entity.living.alien.draco_line.OvamorphDracoRenderer;
import org.avp.client.render.entity.living.alien.runner_line.ChestbursterRunnerRenderer;
import org.avp.client.render.entity.living.alien.runner_line.CrusherRenderer;
import org.avp.client.render.entity.living.alien.runner_line.DroneRunnerRenderer;
import org.avp.client.render.entity.living.alien.runner_line.WarriorRunnerRenderer;
import org.avp.client.render.entity.living.engineer.EngineerRenderer;
import org.avp.client.render.entity.living.yautja.YautjaRenderer;
import org.avp.client.util.EntityRenderData;
import org.avp.common.data.entity.AcidData;
import org.avp.common.data.entity.GrenadeData;
import org.avp.common.data.entity.RocketData;
import org.avp.common.data.entity.living.alien.base_line.BoilerData;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterData;
import org.avp.common.data.entity.living.alien.base_line.ChestbursterQueenData;
import org.avp.common.data.entity.living.alien.base_line.DroneData;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerData;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerRoyalData;
import org.avp.common.data.entity.living.alien.base_line.NauticomorphData;
import org.avp.common.data.entity.living.alien.base_line.OvamorphData;
import org.avp.common.data.entity.living.alien.base_line.PraetorianData;
import org.avp.common.data.entity.living.alien.base_line.QueenData;
import org.avp.common.data.entity.living.alien.base_line.SpitterData;
import org.avp.common.data.entity.living.alien.base_line.UltramorphData;
import org.avp.common.data.entity.living.alien.base_line.WarriorData;
import org.avp.common.data.entity.living.alien.beluga_line.BelugabursterData;
import org.avp.common.data.entity.living.alien.beluga_line.BelugamorphData;
import org.avp.common.data.entity.living.alien.beluga_line.OctohuggerData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconAdultData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconAdultEngineerData;
import org.avp.common.data.entity.living.alien.deacon_line.DeaconData;
import org.avp.common.data.entity.living.alien.deacon_line.TrilobiteBabyData;
import org.avp.common.data.entity.living.alien.deacon_line.TrilobiteData;
import org.avp.common.data.entity.living.alien.draco_line.ChestbursterDracoData;
import org.avp.common.data.entity.living.alien.draco_line.DracomorphData;
import org.avp.common.data.entity.living.alien.draco_line.OvamorphDracoData;
import org.avp.common.data.entity.living.alien.runner_line.ChestbursterRunnerData;
import org.avp.common.data.entity.living.alien.runner_line.CrusherData;
import org.avp.common.data.entity.living.alien.runner_line.DroneRunnerData;
import org.avp.common.data.entity.living.alien.runner_line.WarriorRunnerData;
import org.avp.common.data.entity.living.engineer.EngineerData;
import org.avp.common.data.entity.living.yautja.YautjaData;
import org.avp.common.registry.AVPEntityDataRegistry;
import org.avp.common.registry.holder.AVPHolder;

public class AVPEntityRenderRegistry extends AVPDeferredRegistry<EntityRenderData<? extends Entity>> {

    public static final AVPEntityRenderRegistry INSTANCE = new AVPEntityRenderRegistry();

    protected <T extends Entity> BLHolder<EntityRenderData<? extends Entity>> createHolder(
        BLHolder<EntityType<T>> entityTypeHolder,
        EntityRendererProvider<T> entityRendererProvider
    ) {
        return createHolder(
            entityTypeHolder.getResourceLocation().getPath(),
            () -> new EntityRenderData<>(entityTypeHolder, entityRendererProvider)
        );
    }

    @Override
    protected BLHolder<EntityRenderData<?>> createHolder(String registryName, Supplier<EntityRenderData<?>> supplier) {
        var holder = new AVPHolder<>(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    private AVPEntityRenderRegistry() {
        // Misc. entities
        createHolder(AcidData.INSTANCE.getHolder(), AcidRenderer::new);
        createHolder(GrenadeData.INSTANCE.getHolder(), ThrownItemRenderer::new);
        createHolder(RocketData.INSTANCE.getHolder(), RocketRenderer::new);

        // Misc. aliens
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
                throw new IllegalStateException(
                    "Entity was registered, but does not have a render provider holder! Entity: " + entityData.getHolder()
                        .getResourceLocation()
                );
            }
        });
    }
}

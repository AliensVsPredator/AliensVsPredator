package org.avp.common.entity.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.avp.api.entity.data.EntityData;

public class AVPEntityDataRegistry {

    public static final AVPEntityDataRegistry INSTANCE = new AVPEntityDataRegistry();

    private final Map<String, EntityData<?>> entries = new HashMap<>();

    private final Map<String, EntityData<? extends LivingEntity>> livingEntries = new HashMap<>();

    private final Map<String, EntityData<? extends Mob>> mobEntries = new HashMap<>();

    public Collection<EntityData<?>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public Collection<EntityData<? extends LivingEntity>> getLivingEntries() {
        return Collections.unmodifiableCollection(livingEntries.values());
    }

    public Collection<EntityData<? extends Mob>> getMobEntries() {
        return Collections.unmodifiableCollection(mobEntries.values());
    }

    private void addEntry(EntityData<? extends Entity> entityData) {
        entries.put(entityData.getRegistryName(), entityData);
    }

    private void addLivingEntry(EntityData<? extends LivingEntity> entityData) {
        addEntry(entityData);
        livingEntries.put(entityData.getRegistryName(), entityData);
    }

    private void addMobEntry(EntityData<? extends Mob> entityData) {
        addLivingEntry(entityData);
        mobEntries.put(entityData.getRegistryName(), entityData);
    }

    public void register() {
        addEntry(AcidData.INSTANCE);

        addMobEntry(BelugabursterData.INSTANCE);
        addMobEntry(BelugamorphData.INSTANCE);
        addMobEntry(BoilerData.INSTANCE);

        addMobEntry(ChestbursterData.INSTANCE);
        addMobEntry(ChestbursterDracoData.INSTANCE);
        addMobEntry(ChestbursterQueenData.INSTANCE);
        addMobEntry(ChestbursterRunnerData.INSTANCE);
        addMobEntry(CrusherData.INSTANCE);

        addMobEntry(DeaconAdultData.INSTANCE);
        addMobEntry(DeaconAdultEngineerData.INSTANCE);
        addMobEntry(DeaconData.INSTANCE);
        addMobEntry(DracomorphData.INSTANCE);
        addMobEntry(DroneData.INSTANCE);
        addMobEntry(DroneRunnerData.INSTANCE);

        addMobEntry(EngineerData.INSTANCE);

        addMobEntry(FacehuggerData.INSTANCE);
        addMobEntry(FacehuggerRoyalData.INSTANCE);

        addEntry(GrenadeData.INSTANCE);

        addMobEntry(NauticomorphData.INSTANCE);

        addMobEntry(OctohuggerData.INSTANCE);
        addMobEntry(OvamorphData.INSTANCE);
        addMobEntry(OvamorphDracoData.INSTANCE);

        addMobEntry(PraetorianData.INSTANCE);

        addMobEntry(QueenData.INSTANCE);

        addMobEntry(SpitterData.INSTANCE);

        addMobEntry(TrilobiteBabyData.INSTANCE);
        addMobEntry(TrilobiteData.INSTANCE);

        addMobEntry(UltramorphData.INSTANCE);

        addMobEntry(WarriorData.INSTANCE);
        addMobEntry(WarriorRunnerData.INSTANCE);

        addMobEntry(YautjaData.INSTANCE);
    }

    private AVPEntityDataRegistry() {}
}

package org.avp.common.entity.data.type;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.avp.api.entity.data.EntityData;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AVPEntityDataRegistry {
    public static final AVPEntityDataRegistry INSTANCE = new AVPEntityDataRegistry();

    private final List<EntityData<?>> entries = new ArrayList<>();
    private final List<EntityData<? extends LivingEntity>> livingEntries = new ArrayList<>();
    private final List<EntityData<? extends Mob>> mobEntries = new ArrayList<>();

    public List<EntityData<?>> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<EntityData<? extends LivingEntity>> getLivingEntries() {
        return Collections.unmodifiableList(livingEntries);
    }

    public List<EntityData<? extends Mob>> getMobEntries() {
        return Collections.unmodifiableList(mobEntries);
    }

    private void addEntry(EntityData<? extends Entity> entityData) {
        entries.add(entityData);
    }

    private void addLivingEntry(EntityData<? extends LivingEntity> entityData) {
        addEntry(entityData);
        livingEntries.add(entityData);
    }

    private void addMobEntry(EntityData<? extends Mob> entityData) {
        addLivingEntry(entityData);
        mobEntries.add(entityData);
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

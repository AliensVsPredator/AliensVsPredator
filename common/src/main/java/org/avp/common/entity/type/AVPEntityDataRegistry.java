package org.avp.common.entity.type;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    public List<EntityData<?>> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<EntityData<? extends LivingEntity>> getLivingEntries() {
        return Collections.unmodifiableList(livingEntries);
    }

    private void addEntry(EntityData<? extends Entity> entityData) {
        entries.add(entityData);
    }

    private void addLivingEntry(EntityData<? extends LivingEntity> entityData) {
        addEntry(entityData);
        livingEntries.add(entityData);
    }

    public void register() {
        addEntry(AcidData.INSTANCE);

        addLivingEntry(BelugabursterData.INSTANCE);
        addLivingEntry(BelugamorphData.INSTANCE);
        addLivingEntry(BoilerData.INSTANCE);

        addLivingEntry(ChestbursterData.INSTANCE);
        addLivingEntry(ChestbursterDracoData.INSTANCE);
        addLivingEntry(ChestbursterQueenData.INSTANCE);
        addLivingEntry(ChestbursterRunnerData.INSTANCE);
        addLivingEntry(CrusherData.INSTANCE);

        addLivingEntry(DeaconAdultData.INSTANCE);
        addLivingEntry(DeaconAdultEngineerData.INSTANCE);
        addLivingEntry(DeaconData.INSTANCE);
        addLivingEntry(DracomorphData.INSTANCE);
        addLivingEntry(DroneData.INSTANCE);
        addLivingEntry(DroneRunnerData.INSTANCE);

        addLivingEntry(EngineerData.INSTANCE);

        addLivingEntry(FacehuggerData.INSTANCE);
        addLivingEntry(FacehuggerRoyalData.INSTANCE);

        addLivingEntry(NauticomorphData.INSTANCE);

        addLivingEntry(OctohuggerData.INSTANCE);
        addLivingEntry(OvamorphData.INSTANCE);
        addLivingEntry(OvamorphDracoData.INSTANCE);

        addLivingEntry(PraetorianData.INSTANCE);

        addLivingEntry(QueenData.INSTANCE);

        addLivingEntry(SpitterData.INSTANCE);

        addLivingEntry(TrilobiteBabyData.INSTANCE);
        addLivingEntry(TrilobiteData.INSTANCE);

        addLivingEntry(UltramorphData.INSTANCE);

        addLivingEntry(WarriorData.INSTANCE);
        addLivingEntry(WarriorRunnerData.INSTANCE);

        addLivingEntry(YautjaData.INSTANCE);
    }

    private AVPEntityDataRegistry() {}
}

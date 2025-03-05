package com.avp.core.common.hive.ai.task.impl;

import com.avp.core.common.util.LazySupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

import com.avp.core.common.entity.AVPEntityTypeTags;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.hive.Hive;
import com.avp.core.common.hive.HiveMemberData;
import com.avp.core.common.hive.ai.task.HiveTask;

public class PickBestLeaderTask extends HiveTask {

    private static final int FREQUENCY = 20 * 10;

    private static final LazySupplier<Map<EntityType<?>, Integer>> LEADER_DISPOSITION_BY_TYPE = new LazySupplier<>(() -> Map.ofEntries(
        Map.entry(AVPEntityTypes.DRONE.get(), 0),
        Map.entry(AVPEntityTypes.WARRIOR.get(), 1),
        Map.entry(AVPEntityTypes.PRAETORIAN.get(), 2),
        Map.entry(AVPEntityTypes.QUEEN.get(), 3)
    ));

    public PickBestLeaderTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    @Override
    public void run() {
        Entity candidate = hive.hiveLeaderOrNull();
        HiveMemberData candidateHiveMemberData = candidate == null ? null : hive.hiveMemberDataMap().get(candidate.getUUID());

        for (var hiveMemberEntry : hive.hiveMemberDataMap().entrySet()) {
            var contestant = ((ServerLevel) hive.level()).getEntity(hiveMemberEntry.getKey());
            var contestantHiveMemberData = hiveMemberEntry.getValue();

            if (contestant == null || !contestant.getType().is(AVPEntityTypeTags.XENOMORPHS)) {
                continue;
            }

            if (candidate == null || candidateHiveMemberData == null) {
                candidate = contestant;
                candidateHiveMemberData = contestantHiveMemberData;
                continue;
            }

            if (compareEntityTypes(candidate.getType(), contestant.getType())) {
                candidate = contestant;
                candidateHiveMemberData = contestantHiveMemberData;
            }
        }

        if (candidate != null) {
            hive.setHiveLeaderId(candidate.getUUID());
        }
    }

    public boolean compareEntityTypes(EntityType<?> current, EntityType<?> other) {
        var currentDisposition = LEADER_DISPOSITION_BY_TYPE.get().getOrDefault(current, -1);
        var contestantDisposition = LEADER_DISPOSITION_BY_TYPE.get().getOrDefault(other, -1);
        return currentDisposition < contestantDisposition;
    }
}

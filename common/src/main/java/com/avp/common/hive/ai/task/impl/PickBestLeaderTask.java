package com.avp.common.hive.ai.task.impl;

import com.bvanseg.just.functional.function.Lazy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;
import com.avp.common.hive.ai.task.HiveTask;

public class PickBestLeaderTask extends HiveTask {

    private static final int FREQUENCY = 20 * 10;

    private static final Lazy<Map<EntityType<?>, Integer>> LEADER_DISPOSITION_BY_TYPE = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(TempAVPEntityTypes.DRONE.get(), 0),
            Map.entry(TempAVPEntityTypes.WARRIOR.get(), 1),
            Map.entry(TempAVPEntityTypes.PRAETORIAN.get(), 2),
            Map.entry(TempAVPEntityTypes.QUEEN.get(), 3)
        )
    );

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

package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class PickBestLeaderTask extends HiveTask {

    private static final int FREQUENCY = 20 * 10;

    public PickBestLeaderTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    @Override
    public void run() {
        Entity candidate = hive.getLeadershipManager().getLeaderOrNull();
        var membershipManager = hive.getMembershipManager();
        var candidateHiveMemberData = membershipManager.getMemberData(candidate);

        for (var memberUUID : membershipManager.getMemberUUIDs()) {
            var contestant = ((ServerLevel) hive.level()).getEntity(memberUUID);
            var contestantHiveMemberData = membershipManager.getMemberData(memberUUID);

            if (contestant == null || !contestant.getType().is(AVPEntityTypeTags.XENOMORPHS)) {
                continue;
            }

            if (candidate == null || candidateHiveMemberData.isNone()) {
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
            hive.getLeadershipManager().setLeaderId(candidate.getUUID());
        }
    }

    public boolean compareEntityTypes(EntityType<?> current, EntityType<?> other) {
        var currentDisposition = getDispositionForEntityType(current);
        var contestantDisposition = getDispositionForEntityType(other);
        return currentDisposition < contestantDisposition;
    }

    private int getDispositionForEntityType(EntityType<?> entityType) {
        if (entityType.is(AVPEntityTypeTags.DRONES)) {
            return 0;
        } else if (entityType.is(AVPEntityTypeTags.WARRIORS)) {
            return 1;
        } else if (entityType.is(AVPEntityTypeTags.PRAETORIANS)) {
            return 2;
        } else if (entityType.is(AVPEntityTypeTags.QUEENS)) {
            return 3;
        }

        return -1;
    }
}

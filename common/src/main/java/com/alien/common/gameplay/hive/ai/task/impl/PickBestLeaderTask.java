package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import com.alien.common.gameplay.hive.util.HiveLeaderDispositionUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

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

            if (HiveLeaderDispositionUtil.isLeftLowerDisposition(candidate.getType(), contestant.getType())) {
                candidate = contestant;
                candidateHiveMemberData = contestantHiveMemberData;
            }
        }

        if (candidate != null) {
            hive.getLeadershipManager().setLeaderId(candidate.getUUID());
        }
    }
}

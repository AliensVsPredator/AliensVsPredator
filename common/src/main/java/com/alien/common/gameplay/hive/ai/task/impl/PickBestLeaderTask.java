package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import com.alien.common.gameplay.hive.util.HiveLeaderDispositionUtil;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class PickBestLeaderTask extends HiveTask {

    private static final int TWENTY_SECONDS_IN_TICKS = 20 * 10;

    public PickBestLeaderTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % TWENTY_SECONDS_IN_TICKS == 0;
    }

    @Override
    public void run() {
        var membershipManager = hive.getMembershipManager();
        var leadershipManager = hive.getLeadershipManager();

        var candidateUUID = leadershipManager.getLeaderIdOrNull();
        var candidateHiveMemberData = membershipManager.getMemberData(candidateUUID).unwrapOr(null);

        for (var contestantUUID : membershipManager.getMemberUUIDs()) {
            var contestantHiveMemberDataOption = membershipManager.getMemberData(contestantUUID);

            if (contestantHiveMemberDataOption.isNone()) {
                // Contestant is not a hive member, skip.
                continue;
            }

            var contestantHiveMemberData = contestantHiveMemberDataOption.unwrap();
            var contestantType = contestantHiveMemberData.getEntityType().unwrapOr(null);

            if (contestantType == null || !contestantType.is(AVPEntityTypeTags.XENOMORPHS)) {
                // Invalid contestant, skip.
                continue;
            }

            var candidateType = candidateHiveMemberData == null
                ? null
                : candidateHiveMemberData.getEntityType().unwrapOr(null);

            if (candidateHiveMemberData == null || candidateType == null) {
                // Current candidate is null, so it can't compete against the current contestant.
                // Current contestant wins by default.
                candidateUUID = contestantUUID;
                candidateHiveMemberData = contestantHiveMemberData;
                continue;
            }

            // Both candidate type and contestant type should be non-null by this point.
            if (HiveLeaderDispositionUtil.isLeftLowerDisposition(candidateType, contestantType)) {
                // The contestant had a higher disposition/ranking over the current candidate.
                // The current contestant wins.
                candidateUUID = contestantUUID;
                candidateHiveMemberData = contestantHiveMemberData;
            }
        }

        leadershipManager.setLeaderId(candidateUUID);
    }
}

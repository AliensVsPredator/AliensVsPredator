package com.avp.core.common.hive.ai.task.impl;

import com.avp.core.common.hive.Hive;
import com.avp.core.common.hive.ai.task.HiveTask;

public class UpdateHiveMembershipsTask extends HiveTask {

    private static final int FREQUENCY = 20 * 60;

    public UpdateHiveMembershipsTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    @Override
    public void run() {
        hive.hiveMemberDataMap().entrySet().removeIf(hiveMemberEntry -> {
            var hiveMemberData = hiveMemberEntry.getValue();
            var lastSeenTimestampInTicks = hiveMemberData.lastSeenTimestampInTicks();
            var lastSeenPos = hiveMemberData.lastSeenPos();
            var isLastSeenPosLoaded = hive.level().isLoaded(lastSeenPos);

            if (!isLastSeenPosLoaded) {
                return true;
            }

            // Remove the hive member if their last seen pos is loaded and if they haven't been seen in 3 minutes.
            return hive.ageInTicks() - lastSeenTimestampInTicks > 20 * 60 * 3;
        });

        if (!hive.hiveMemberDataMap().containsKey(hive.hiveLeaderId())) {
            // If the hive leader id is no longer present in the hive member data map, clear the leader.
            hive.setHiveLeaderId(null);
        }
    }
}

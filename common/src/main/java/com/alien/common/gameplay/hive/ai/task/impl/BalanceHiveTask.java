package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;

import java.util.Objects;

public abstract class BalanceHiveTask extends HiveTask {

    private static final int FREQUENCY = 20 * 60 * 5;

    protected BalanceHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    protected void growXenomorph(Xenomorph xenomorph) {
        var nextFormEntity = xenomorph.getGrowthManager().grow();

        if (nextFormEntity == null) {
            return;
        }

        var isLeader = Objects.equals(xenomorph.getUUID(), hive.getLeadershipManager().getLeaderIdOrNull());

        // Remove the old entity's membership.
        hive.getMembershipManager().removeMember(xenomorph);
        // Add the new entity as a member.
        hive.getMembershipManager().addMember(nextFormEntity);

        if (isLeader) {
            hive.getLeadershipManager().setLeaderId(nextFormEntity.getUUID());
        }
    }
}

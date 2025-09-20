package com.alien.common.gameplay.hive.ai.task.impl.balance;

import com.alien.common.gameplay.entity.living.alien.GrowthManager;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import net.minecraft.world.entity.Entity;

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
        var growthResult = xenomorph.getGrowthManager().grow();

        switch (growthResult) {
            case GrowthManager.GrowthResult.AlreadyFullyGrown $ -> {/* NO-OP */}
            case GrowthManager.GrowthResult.CanNotGrow $ -> {/* NO-OP */}
            case GrowthManager.GrowthResult.Success success -> handleXenomorphGrowth(xenomorph, success.newEntity());
            case GrowthManager.GrowthResult.FailedTransitionResult $ -> {/* NO-OP */}
        }
    }

    private void handleXenomorphGrowth(Xenomorph xenomorph, Entity grownEntity) {
        var isLeader = Objects.equals(xenomorph.getUUID(), hive.getLeadershipManager().getLeaderIdOrNull());

        // Remove the old entity's membership.
        hive.getMembershipManager().removeMember(xenomorph);
        // Add the new entity as a member.
        hive.getMembershipManager().addMember(grownEntity);

        if (isLeader) {
            hive.getLeadershipManager().setLeaderId(grownEntity.getUUID());
        }
    }
}

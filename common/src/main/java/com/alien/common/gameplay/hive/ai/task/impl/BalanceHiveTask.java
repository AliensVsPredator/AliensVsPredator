package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import com.alien.common.model.hive.HiveMemberData;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class BalanceHiveTask extends HiveTask {

    private static final int FREQUENCY = 20 * 60 * 5;

    protected BalanceHiveTask(Hive hive) {
        super(hive);
    }

    protected abstract void balance(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType);

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    @Override
    public void run() {
        balance(hive.getMembershipManager().getMembersByEntityType());
    }

    protected void growXenomorph(Xenomorph xenomorph) {
        var nextFormEntity = xenomorph.getGrowthManager().grow();

        if (nextFormEntity != null) {
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
}

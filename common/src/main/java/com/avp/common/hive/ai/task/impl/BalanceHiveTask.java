package com.avp.common.hive.ai.task.impl;

import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;
import com.avp.common.hive.ai.task.HiveTask;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;

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
        var type = xenomorph.getType();
        var growthStage = AlienLifecycleRegistry.getOrNull(null, type);

        // TODO: Don't duplicate this check here, the growth manager should already be checking this.
        if (
            growthStage == null
                || xenomorph.isPoisoned()
                || xenomorph.isIrradiated()
        ) {
            return;
        }

        var nextFormEntity = xenomorph.getGrowthManager().grow(growthStage);

        if (nextFormEntity != null) {
            var isLeader = Objects.equals(xenomorph.getUUID(), hive.hiveLeaderId());

            // Remove the old entity's membership.
            hive.getMembershipManager().removeMember(xenomorph);
            // Add the new entity as a member.
            hive.getMembershipManager().addMember(nextFormEntity);

            if (isLeader) {
                hive.setHiveLeaderId(nextFormEntity.getUUID());
            }
        }
    }
}

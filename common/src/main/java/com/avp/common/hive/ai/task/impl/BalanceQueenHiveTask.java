package com.avp.common.hive.ai.task.impl;

import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;

public class BalanceQueenHiveTask extends BalanceHiveTask {

    public BalanceQueenHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    protected void balance(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var queens = membersByType.getOrDefault(AVPEntityTypes.QUEEN.get(), List.of());

        if (!queens.isEmpty()) {
            return;
        }

        hive.getLeadershipManager().getLeader().ifSome(hiveLeader -> {
            if (!(hiveLeader instanceof Xenomorph xenomorph)) {
                // If the hive leader is not a xenomorph (somehow), then return.
                return;
            }

            var hiveLeaderDataOption = hive.getMembershipManager().getMemberData(xenomorph.getUUID());

            hiveLeaderDataOption.ifSome($ -> growXenomorph(xenomorph));
        });
    }
}

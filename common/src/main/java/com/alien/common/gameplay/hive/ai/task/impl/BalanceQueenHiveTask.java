package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.model.hive.HiveMemberData;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceQueenHiveTask extends BalanceHiveTask {

    public BalanceQueenHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    protected void balance(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var queenEntityType = Queen.getType(hive.getVariant());
        var queens = membersByType.getOrDefault(queenEntityType, List.of());

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

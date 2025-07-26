package com.alien.common.gameplay.hive.ai.task.impl.balance;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.hive.Hive;

import java.util.List;

public class BalanceQueenHiveTask extends BalanceHiveTask {

    public BalanceQueenHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public void run() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
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

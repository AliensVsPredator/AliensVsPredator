package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.hive.Hive;
import net.minecraft.server.level.ServerLevel;

import java.util.Comparator;
import java.util.List;

public class BalanceRunnersAndProwlersHiveTask extends BalanceHiveTask {

    public BalanceRunnersAndProwlersHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public void run() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
        var runnerEntityType = Runner.getType(hive.getVariant());
        var prowlerEntityType = Prowler.getType(hive.getVariant());
        var runners = membersByType.getOrDefault(runnerEntityType, List.of());
        var prowlers = membersByType.getOrDefault(prowlerEntityType, List.of());

        var desiredProwlerCount = Math.max(0, (runners.size() - prowlers.size()) / 2);

        if (desiredProwlerCount == 0) {
            return;
        }

        var runnerCount = runners.size();
        var offset = Math.max(runnerCount - desiredProwlerCount, 0);

        runners.stream()
            .sorted(Comparator.comparingInt(a -> a.getValue().lastSeenTimestampInTicks()))
            .toList()
            .subList(offset, runnerCount)
            .forEach(entry -> {
                var runner = ((ServerLevel) hive.level()).getEntity(entry.getKey());

                if (!(runner instanceof Xenomorph xenomorph)) {
                    return;
                }

                growXenomorph(xenomorph);
            });
    }
}

package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.gameplay.hive.Hive;
import net.minecraft.server.level.ServerLevel;

import java.util.Comparator;
import java.util.List;

import com.avp.AVP;

public class BalanceCrushersHiveTask extends BalanceHiveTask {

    public BalanceCrushersHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public void run() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
        var hiveMemberCount = hive.getMembershipManager().getMemberCount();
        var prowlerEntityType = Prowler.getType(hive.getVariant());
        var crusherEntityType = Crusher.getType(hive.getVariant());
        var prowlers = membersByType.getOrDefault(prowlerEntityType, List.of());
        var crushers = membersByType.getOrDefault(crusherEntityType, List.of());

        int hiveMembersRequiredForCrusher = AVP.config.hiveConfigs.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN;
        int maxCrusherCount = AVP.config.hiveConfigs.HIVE_MAX_PRAETORIAN_COUNT;
        var desiredCrusherCount = hiveMembersRequiredForCrusher > 0
            ? Math.max(0, Math.clamp(hiveMemberCount / hiveMembersRequiredForCrusher, 0, maxCrusherCount) - crushers.size())
            : 0;

        if (desiredCrusherCount == 0) {
            return;
        }

        var prowlerCount = prowlers.size();
        var offset = Math.max(prowlerCount - desiredCrusherCount, 0);

        prowlers.stream()
            .sorted(Comparator.comparingInt(a -> a.getValue().lastSeenTimestampInTicks()))
            .toList()
            .subList(offset, prowlerCount)
            .forEach(entry -> {
                var prowler = ((ServerLevel) hive.level()).getEntity(entry.getKey());

                if (!(prowler instanceof Xenomorph xenomorph)) {
                    return;
                }

                growXenomorph(xenomorph);
            });
    }
}

package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.gameplay.hive.Hive;
import net.minecraft.server.level.ServerLevel;

import java.util.Comparator;
import java.util.List;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class BalancePraetoriansHiveTask extends BalanceHiveTask {

    public BalancePraetoriansHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public void run() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
        var xenomorphHiveMemberCount = hive.getMembershipManager()
            .getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS))
            .size();
        var warriorEntityType = Warrior.getType(hive.getVariant());
        var praetorianEntityType = Praetorian.getType(hive.getVariant());
        var warriors = membersByType.getOrDefault(warriorEntityType, List.of());
        var praetorians = membersByType.getOrDefault(praetorianEntityType, List.of());

        int hiveMembersRequiredForPraetorian = AVP.config.hiveConfigs.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN;
        int maxPraetorianCount = AVP.config.hiveConfigs.HIVE_MAX_PRAETORIAN_COUNT;
        var desiredPraetorianCount = hiveMembersRequiredForPraetorian > 0
            ? Math.max(
                0,
                Math.clamp(xenomorphHiveMemberCount / hiveMembersRequiredForPraetorian, 0, maxPraetorianCount) - praetorians.size()
            )
            : 0;

        if (desiredPraetorianCount == 0) {
            return;
        }

        var warriorCount = warriors.size();
        var offset = Math.max(warriorCount - desiredPraetorianCount, 0);

        warriors.stream()
            .sorted(Comparator.comparingInt(a -> a.getValue().lastSeenTimestampInTicks()))
            .toList()
            .subList(offset, warriorCount)
            .forEach(entry -> {
                var warrior = ((ServerLevel) hive.level()).getEntity(entry.getKey());

                if (!(warrior instanceof Xenomorph xenomorph)) {
                    return;
                }

                growXenomorph(xenomorph);
            });
    }
}

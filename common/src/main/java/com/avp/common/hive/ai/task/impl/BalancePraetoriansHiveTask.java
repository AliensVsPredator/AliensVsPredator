package com.avp.common.hive.ai.task.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.avp.AVP;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;

public class BalancePraetoriansHiveTask extends BalanceHiveTask {

    public BalancePraetoriansHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    protected void balance(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var hiveMemberCount = hive.getMembershipManager().getMemberCount();
        var warriors = membersByType.getOrDefault(AVPEntityTypes.WARRIOR.get(), List.of());
        var praetorians = membersByType.getOrDefault(AVPEntityTypes.PRAETORIAN.get(), List.of());

        int hiveMembersRequiredForPraetorian = AVP.config.hiveConfigs.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN;
        int maxPraetorianCount = AVP.config.hiveConfigs.HIVE_MAX_PRAETORIAN_COUNT;
        var desiredPraetorianCount = hiveMembersRequiredForPraetorian > 0
            ? Math.max(0, Math.clamp(hiveMemberCount / hiveMembersRequiredForPraetorian, 0, maxPraetorianCount) - praetorians.size())
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

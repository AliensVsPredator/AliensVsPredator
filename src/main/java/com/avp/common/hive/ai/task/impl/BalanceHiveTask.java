package com.avp.common.hive.ai.task.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;
import com.avp.common.hive.ai.task.HiveTask;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;

public class BalanceHiveTask extends HiveTask {

    private static final int FREQUENCY = 20 * 60 * 5;

    public BalanceHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return hive.ageInTicks() % FREQUENCY == 0;
    }

    @Override
    public void run() {
        var membersByType = hive.hiveMemberDataMap()
            .entrySet()
            .stream()
            .collect(Collectors.groupingBy(entry -> BuiltInRegistries.ENTITY_TYPE.get(entry.getValue().entityType())));

        balanceDronesAndWarriors(membersByType);
        balancePraetorians(membersByType);
        balanceQueen(membersByType);
    }

    private void balanceDronesAndWarriors(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var drones = membersByType.getOrDefault(AVPEntityTypes.DRONE, List.of());
        var warriors = membersByType.getOrDefault(AVPEntityTypes.WARRIOR, List.of());

        var desiredWarriorCount = Math.max(0, (drones.size() - warriors.size()) / 2);

        if (desiredWarriorCount == 0) {
            return;
        }

        var droneCount = drones.size();
        var offset = Math.max(droneCount - desiredWarriorCount, 0);

        drones.stream()
            .sorted(Comparator.comparingInt(a -> a.getValue().lastSeenTimestampInTicks()))
            .toList()
            .subList(offset, droneCount)
            .forEach(entry -> {
                var drone = ((ServerLevel) hive.level()).getEntity(entry.getKey());

                if (!(drone instanceof Xenomorph xenomorph)) {
                    return;
                }

                growXenomorph(entry.getValue(), xenomorph);
            });
    }

    private void balancePraetorians(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var hiveMemberCount = hive.hiveMemberDataMap().size();
        var warriors = membersByType.getOrDefault(AVPEntityTypes.WARRIOR, List.of());
        var praetorians = membersByType.getOrDefault(AVPEntityTypes.PRAETORIAN, List.of());

        var properties = AVP.HIVES_CONFIG.properties();
        int hiveMembersRequiredForPraetorian = properties.getOrThrow(ConfigProperties.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN);
        int maxPraetorianCount = properties.getOrThrow(ConfigProperties.HIVE_MAX_PRAETORIAN_COUNT);
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

                growXenomorph(entry.getValue(), xenomorph);
            });
    }

    private void balanceQueen(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var queens = membersByType.getOrDefault(AVPEntityTypes.QUEEN, List.of());

        if (!queens.isEmpty()) {
            return;
        }

        hive.hiveLeader().ifPresent(hiveLeader -> {
            if (!Objects.equals(hiveLeader.getType(), AVPEntityTypes.PRAETORIAN) || !(hiveLeader instanceof Xenomorph xenomorph)) {
                return;
            }

            var hiveLeaderData = hive.hiveMemberDataMap().get(xenomorph.getUUID());

            if (hiveLeaderData != null) {
                growXenomorph(hiveLeaderData, xenomorph);
            }
        });
    }

    private void growXenomorph(HiveMemberData oldHiveMemberData, Xenomorph xenomorph) {
        var type = xenomorph.getType();
        var growthStage = AlienLifecycleRegistry.getOrNull(null, type);

        if (growthStage == null) {
            return;
        }

        var nextFormType = growthStage.to();
        var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(nextFormType);

        var nextFormEntity = xenomorph.growthManager().grow(growthStage);

        if (nextFormEntity != null) {
            var isLeader = Objects.equals(xenomorph.getUUID(), hive.hiveLeaderId());

            var newHiveMemberData = new HiveMemberData(
                resourceLocation,
                oldHiveMemberData.lastSeenPos(),
                oldHiveMemberData.lastSeenTimestampInTicks()
            );

            // Remove the old entity by the old entity's id.
            hive.hiveMemberDataMap().remove(xenomorph.getUUID());
            // Add the new entity by the new entity's id.
            hive.hiveMemberDataMap().put(nextFormEntity.getUUID(), newHiveMemberData);

            if (isLeader) {
                hive.setHiveLeaderId(nextFormEntity.getUUID());
            }
        }
    }
}

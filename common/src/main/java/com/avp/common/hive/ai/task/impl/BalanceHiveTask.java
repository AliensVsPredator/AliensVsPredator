package com.avp.common.hive.ai.task.impl;

import com.bvanseg.just.functional.option.Option;
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
        var membersByType = hive.getMembershipManager()
            .getMemberUUIDs()
            .stream()
            .map(uuid -> hive.getMembershipManager().getMemberData(uuid).map(data -> Map.entry(uuid, data)))
            .flatMap(Option::toStream)
            .collect(Collectors.groupingBy(entry -> BuiltInRegistries.ENTITY_TYPE.get(entry.getValue().entityType())));

        balanceDronesAndWarriors(membersByType);
        balancePraetorians(membersByType);
        balanceQueen(membersByType);
    }

    private void balanceDronesAndWarriors(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var drones = membersByType.getOrDefault(AVPEntityTypes.DRONE.get(), List.of());
        var warriors = membersByType.getOrDefault(AVPEntityTypes.WARRIOR.get(), List.of());

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

                growXenomorph(xenomorph);
            });
    }

    private void balancePraetorians(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var hiveMemberCount = hive.getMembershipManager().getMemberCount();
        var warriors = membersByType.getOrDefault(AVPEntityTypes.WARRIOR.get(), List.of());
        var praetorians = membersByType.getOrDefault(AVPEntityTypes.PRAETORIAN.get(), List.of());
        var queens = membersByType.getOrDefault(AVPEntityTypes.QUEEN.get(), List.of());

        int hiveMembersRequiredForPraetorian = AVP.config.hiveConfigs.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN;
        int maxPraetorianCount = AVP.config.hiveConfigs.HIVE_MAX_PRAETORIAN_COUNT;
        var desiredPraetorianCount = hiveMembersRequiredForPraetorian > 0
            ? Math.max(0, Math.clamp(hiveMemberCount / hiveMembersRequiredForPraetorian, 0, maxPraetorianCount) - praetorians.size())
            : 0;

        if (desiredPraetorianCount == 0 || !queens.isEmpty() || !hive.isChunkLoaded()) {
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

    private void balanceQueen(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var queens = membersByType.getOrDefault(AVPEntityTypes.QUEEN.get(), List.of());

        if (!queens.isEmpty()) {
            return;
        }

        hive.hiveLeader().ifSome(hiveLeader -> {
            if (!(hiveLeader instanceof Xenomorph xenomorph)) {
                // If the hive leader is not a xenomorph (somehow), then return.
                return;
            }

            var hiveLeaderDataOption = hive.getMembershipManager().getMemberData(xenomorph.getUUID());

            hiveLeaderDataOption.ifSome($ -> growXenomorph(xenomorph));
        });
    }

    private void growXenomorph(Xenomorph xenomorph) {
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

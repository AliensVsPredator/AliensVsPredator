package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.HiveRemovalReason;
import com.alien.common.gameplay.hive.ai.task.HiveTask;
import com.alien.common.gameplay.hive.util.HiveLeaderDispositionUtil;
import com.alien.common.gameplay.level.saveddata.HiveLevelData;

import java.util.Map;
import java.util.Objects;

public class MergeWithNearbyHiveTask extends HiveTask {

    public MergeWithNearbyHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    public boolean canRun() {
        return super.canRun()
            && !hive.isAngry()
            && hive.ageInTicks() % (20 * 60) == 0;
    }

    @Override
    public void run() {
        var nearestHive = HiveLevelData.getOrCreate(hive.level())
            .andThen(
                hiveLevelData -> hiveLevelData.findNearestHive(
                    hive.centerPosition(),
                    hive -> !Objects.equals(hive.id(), this.hive.id())
                        && Objects.equals(hive.getVariant(), this.hive.getVariant())
                )
            )
            .unwrapOr(null);

        if (nearestHive == null) {
            return;
        }

        var neighborHiveCenter = nearestHive.centerPosition();

        // Hives must be within the leash layer or lower to consider merging.
        if (!hive.getSpaceManager().isBlockPosLeashedToHive(neighborHiveCenter)) {
            return;
        }

        // At this point we know there is a same-variant hive within our leash layer. We now have two choices:
        // - merge this hive into the neighbor hive
        // - merge the neighbor hive into this hive
        // To determine which is more appropriate, we assume that the more powerful hive should absorb the weaker hive.
        var strongerHive = getStrongerHive(hive, nearestHive);
        var weakerHive = Objects.equals(strongerHive.id(), hive.id())
            ? nearestHive
            : hive;

        mergeLeftHiveIntoRight(weakerHive, strongerHive);
    }

    private Hive getStrongerHive(Hive left, Hive right) {
        var leftLeader = left.getLeadershipManager().getLeaderOrNull();
        var rightLeader = right.getLeadershipManager().getLeaderOrNull();

        if (leftLeader != null && rightLeader != null) {
            var leftDisposition = HiveLeaderDispositionUtil.getDispositionForEntityType(leftLeader.getType());
            var rightDisposition = HiveLeaderDispositionUtil.getDispositionForEntityType(rightLeader.getType());

            if (leftDisposition > rightDisposition) {
                return left;
            } else if (leftDisposition < rightDisposition) {
                return right;
            }
        }

        var leftMemberCount = left.getMembershipManager().getMemberCount();
        var rightMemberCount = right.getMembershipManager().getMemberCount();

        if (leftMemberCount > rightMemberCount) {
            return left;
        } else if (leftMemberCount < rightMemberCount) {
            return right;
        }

        var leftAgeInTicks = left.ageInTicks();
        var rightAgeInTicks = right.ageInTicks();

        if (leftAgeInTicks > rightAgeInTicks) {
            return left;
        } else if (leftAgeInTicks < rightAgeInTicks) {
            return right;
        }

        return left;
    }

    private void mergeLeftHiveIntoRight(Hive left, Hive right) {
        // Gather the members that we need to migrate over.
        var leftMemberData = left.getMembershipManager()
            .getMemberUUIDs()
            .stream()
            .flatMap(
                uuid -> left.getMembershipManager()
                    .getMemberData(uuid)
                    .map(hiveMemberData -> Map.entry(uuid, hiveMemberData))
                    .toStream()
            )
            .toList();

        // Migrate the members from left hive to right hive.
        leftMemberData.forEach(
            entry -> right.getMembershipManager()
                .addMember(entry.getKey(), entry.getValue())
        );

        // Clear members from left hive after we've moved them.
        left.getMembershipManager().clearMembers();
        // Clear the left hive's leader.
        left.getLeadershipManager().setLeaderId(null);
        // Finally remove the left hive.
        left.remove(HiveRemovalReason.DISCARDED);
    }
}

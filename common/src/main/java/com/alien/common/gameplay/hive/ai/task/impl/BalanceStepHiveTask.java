package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.hive.Hive;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class BalanceStepHiveTask extends BalanceHiveTask {

    private static final Predicate<EntityType<?>> XENOMORPH_PREDICATE = entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS);

    private final Supplier<EntityType<?>> baseUnitTypeSupplier;

    private final Supplier<EntityType<?>> desiredUnitTypeSupplier;

    public BalanceStepHiveTask(
        Hive hive,
        Supplier<EntityType<?>> baseUnitTypeSupplier,
        Supplier<EntityType<?>> desiredUnitTypeSupplier
    ) {
        super(hive);
        this.baseUnitTypeSupplier = baseUnitTypeSupplier;
        this.desiredUnitTypeSupplier = desiredUnitTypeSupplier;
    }

    @Override
    public void run() {
        balanceReserveUnits();
        balanceLoadedUnits();
    }

    private void balanceReserveUnits() {
        var reserveManager = hive.getReserveManager();
        var reserveXenomorphCount = reserveManager
            .getCountMatching(XENOMORPH_PREDICATE);

        var baseUnitType = baseUnitTypeSupplier.get();
        var desiredUnitType = desiredUnitTypeSupplier.get();
        var currentDesiredUnitCount = reserveManager.getCount(desiredUnitType);

        var desiredUnitCount = computeDesiredUnitCount(reserveXenomorphCount, currentDesiredUnitCount);

        if (desiredUnitCount == 0) {
            return;
        }

        // Subtract from base unit count by adding a negative.
        reserveManager.add(baseUnitType, -desiredUnitCount);
        // Add desired unit count to desired unit type.
        reserveManager.add(desiredUnitType, desiredUnitCount);
    }

    private void balanceLoadedUnits() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
        var xenomorphHiveMemberCount = hive.getMembershipManager()
            .getMembersMatching(XENOMORPH_PREDICATE)
            .size();
        var baseEntityType = baseUnitTypeSupplier.get();
        var desiredEntityType = desiredUnitTypeSupplier.get();
        var baseUnits = membersByType.getOrDefault(baseEntityType, List.of());
        var baseUnitCount = baseUnits.size();
        var desiredUnits = membersByType.getOrDefault(desiredEntityType, List.of());
        var currentDesiredUnitCount = desiredUnits.size();

        var desiredUnitCount = computeDesiredUnitCount(xenomorphHiveMemberCount, currentDesiredUnitCount);

        if (desiredUnitCount == 0) {
            return;
        }

        var offset = Math.max(baseUnitCount - desiredUnitCount, 0);

        baseUnits.stream()
            .sorted(Comparator.comparingInt(a -> a.getValue().lastSeenTimestampInTicks()))
            .toList()
            .subList(offset, baseUnitCount)
            .forEach(entry -> {
                var baseUnit = ((ServerLevel) hive.level()).getEntity(entry.getKey());

                if (!(baseUnit instanceof Xenomorph xenomorph)) {
                    return;
                }

                growXenomorph(xenomorph);
            });
    }

    private int computeDesiredUnitCount(int xenomorphHiveMemberCount, int desiredUnitCount) {
        var hiveMembersRequiredForPraetorian = AVP.config.hiveConfigs.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN;
        var maxPraetorianCount = AVP.config.hiveConfigs.HIVE_MAX_PRAETORIAN_COUNT;

        return hiveMembersRequiredForPraetorian > 0
            ? Math.max(
                0,
                Math.clamp(xenomorphHiveMemberCount / hiveMembersRequiredForPraetorian, 0, maxPraetorianCount) - desiredUnitCount
            )
            : 0;
    }
}

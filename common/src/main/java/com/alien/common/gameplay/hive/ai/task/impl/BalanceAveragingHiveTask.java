package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.hive.Hive;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class BalanceAveragingHiveTask extends BalanceHiveTask {

    private final Supplier<EntityType<?>> baseUnitTypeSupplier;

    private final Supplier<EntityType<?>> desiredUnitTypeSupplier;

    public BalanceAveragingHiveTask(
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
        var baseUnitType = baseUnitTypeSupplier.get();
        var desiredUnitType = desiredUnitTypeSupplier.get();
        var baseUnitCount = reserveManager.getCount(baseUnitType);
        var currentDesiredUnitCount = reserveManager.getCount(desiredUnitType);

        var desiredUnitCount = computeDesiredUnitCount(baseUnitCount, currentDesiredUnitCount);

        if (desiredUnitCount == 0) {
            return;
        }

        reserveManager.decrease(baseUnitType, desiredUnitCount);
        reserveManager.increase(desiredUnitType, desiredUnitCount);
    }

    private void balanceLoadedUnits() {
        var membersByType = hive.getMembershipManager().getMembersByEntityType();
        var baseEntityType = baseUnitTypeSupplier.get();
        var desiredEntityType = desiredUnitTypeSupplier.get();
        var baseUnits = membersByType.getOrDefault(baseEntityType, List.of());
        var baseUnitCount = baseUnits.size();
        var desiredUnits = membersByType.getOrDefault(desiredEntityType, List.of());

        var desiredUnitCount = computeDesiredUnitCount(baseUnitCount, desiredUnits.size());

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

    private int computeDesiredUnitCount(int baseUnitCount, int desiredUnitCount) {
        return Math.max(0, (baseUnitCount - desiredUnitCount) / 2);
    }
}

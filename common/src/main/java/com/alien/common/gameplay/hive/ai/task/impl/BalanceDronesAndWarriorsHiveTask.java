package com.alien.common.gameplay.hive.ai.task.impl;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.model.hive.HiveMemberData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceDronesAndWarriorsHiveTask extends BalanceHiveTask {

    public BalanceDronesAndWarriorsHiveTask(Hive hive) {
        super(hive);
    }

    @Override
    protected void balance(Map<? extends EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> membersByType) {
        var droneEntityType = Drone.getType(hive.getVariant());
        var warriorEntityType = Warrior.getType(hive.getVariant());
        var drones = membersByType.getOrDefault(droneEntityType, List.of());
        var warriors = membersByType.getOrDefault(warriorEntityType, List.of());

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
}

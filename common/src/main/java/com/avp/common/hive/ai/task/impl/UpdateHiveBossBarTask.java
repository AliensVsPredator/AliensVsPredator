package com.avp.common.hive.ai.task.impl;

import net.minecraft.server.level.ServerBossEvent;

import com.avp.common.entity.living.alien.util.AlienPredicates;
import com.avp.common.hive.Hive;
import com.avp.common.hive.ai.task.HiveTask;

public class UpdateHiveBossBarTask extends HiveTask {

    private int maximumSeenAlienCount;

    public UpdateHiveBossBarTask(Hive hive) {
        super(hive);
        this.maximumSeenAlienCount = hive.getXenomorphCount();
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void run() {
        var currentAlienCount = hive.getXenomorphCount();
        this.maximumSeenAlienCount = Math.max(maximumSeenAlienCount, currentAlienCount);

        var bossEvent = hive.bossEvent();

        bossEvent.setProgress(currentAlienCount / (float) maximumSeenAlienCount);

        if (hive.ageInTicks() % 20 == 0) {
            removeNonTargetPlayers(bossEvent);
        }

        if (!hive.isAlive()) {
            bossEvent.removeAllPlayers();
        }
    }

    private void removeNonTargetPlayers(ServerBossEvent bossEvent) {
        var playersToRemove = bossEvent.getPlayers()
            .stream()
            .filter(player -> {
                if (!AlienPredicates.isValidTarget(player)) {
                    return true;
                }

                return !hive.isEntityWithinRangeOfHive(player);
            })
            .toList();

        playersToRemove.forEach(bossEvent::removePlayer);
    }
}

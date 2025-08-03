package com.avp.server;

import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import com.human.common.gameplay.power.PowerSystem;
import com.lib.common.data.Cooldown;
import net.minecraft.server.level.ServerLevel;

import java.time.Duration;

public class ServerLevelManager {

    private final Cooldown queenSpawnCooldown;

    public ServerLevelManager() {
        this.queenSpawnCooldown = Cooldown.withCooldownTime("queenSpawnCooldownInTicks", Duration.ofMinutes(5));

        queenSpawnCooldown.reset();
    }

    public void tick(ServerLevel serverLevel) {
        tickScheduledRunnables();

        queenSpawnCooldown.tick();

        HiveLevelData.getOrCreate(serverLevel)
            .ifSome(HiveLevelData::tick);

        BlockBreakProgressManager.tick(serverLevel);

        PowerSystem.get(serverLevel).tick();
    }

    private void tickScheduledRunnables() {
        ServerScheduler.getScheduledTasks().removeIf(entry -> {
            var runTime = entry.getKey();

            if (System.currentTimeMillis() >= runTime) {
                entry.getValue().run();
                return true;
            }

            return false;
        });
    }

    public Cooldown getQueenSpawnCooldown() {
        return queenSpawnCooldown;
    }
}

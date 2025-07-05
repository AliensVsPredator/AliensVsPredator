package com.avp.server;

import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import com.lib.common.data.Cooldown;
import net.minecraft.server.level.ServerLevel;

import java.time.Duration;

public class ServerLevelManager {

    private final Cooldown queenSpawnCooldown;

    public ServerLevelManager() {
        this.queenSpawnCooldown = Cooldown.withCooldownTime("queenSpawnCooldownInTicks", Duration.ofSeconds(20));

        queenSpawnCooldown.reset();
    }

    public void tick(ServerLevel serverLevel) {
        tickScheduledRunnables();

        var hiveLevelDataOption = HiveLevelData.getOrCreate(serverLevel);

        queenSpawnCooldown.tick();

        hiveLevelDataOption.ifSome(HiveLevelData::tick);

        BlockBreakProgressManager.tick(serverLevel);
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

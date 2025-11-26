package com.avp.server;

import com.human.common.gameplay.power.PowerSystem;
import net.minecraft.server.level.ServerLevel;

public class ServerLevelManager {

    public ServerLevelManager() {}

    public void tick(ServerLevel serverLevel) {
        tickScheduledRunnables();

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
}

package com.blib.api.common.server.v1;

import net.minecraft.world.level.Level;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScheduler {

    private static final Map<Long, Runnable> SCHEDULED_TASKS = new ConcurrentHashMap<>();

    public static void schedule(Runnable runnable, Duration duration) {
        var runTime = System.currentTimeMillis() + duration.toMillis();
        SCHEDULED_TASKS.put(runTime, runnable);
    }

    public static void tick(Level level) {
        SCHEDULED_TASKS.entrySet().removeIf(entry -> {
            var runTime = entry.getKey();

            if (System.currentTimeMillis() >= runTime) {
                entry.getValue().run();
                return true;
            }

            return false;
        });
    }

    private ServerScheduler() {
        throw new UnsupportedOperationException();
    }
}

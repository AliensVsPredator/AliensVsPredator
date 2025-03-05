package com.avp.core.server;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScheduler {

    private static final Map<Long, Runnable> SCHEDULED_TASKS = new ConcurrentHashMap<>();

    public static void schedule(Runnable runnable, Duration duration) {
        var runTime = System.currentTimeMillis() + duration.toMillis();
        SCHEDULED_TASKS.put(runTime, runnable);
    }

    public static Set<Map.Entry<Long, Runnable>> getScheduledTasks() {
        return SCHEDULED_TASKS.entrySet();
    }

    private ServerScheduler() {
        throw new UnsupportedOperationException();
    }
}

package org.avp.api.server;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerScheduler {

    private static final Map<Long, Runnable> SCHEDULED_TASKS = new HashMap<>();

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

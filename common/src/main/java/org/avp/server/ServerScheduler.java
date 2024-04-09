package org.avp.server;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.avp.api.Tuple;

public class ServerScheduler {

    private static final List<Tuple<Long, Runnable>> SCHEDULED_TASKS = new ArrayList<>();

    public static void schedule(Runnable runnable, Duration duration) {
        var runTime = System.currentTimeMillis() + duration.toMillis();
        SCHEDULED_TASKS.add(new Tuple<>(runTime, runnable));
    }

    public static List<Tuple<Long, Runnable>> getScheduledTasks() {
        return SCHEDULED_TASKS;
    }

    private ServerScheduler() {
        throw new UnsupportedOperationException();
    }
}

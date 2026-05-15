package com.blib.internal.common.util;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public final class BLibSaveTiming {

    private static final String PREFIX = "[BLib save timing]";

    private BLibSaveTiming() {}

    public static void time(Logger logger, String label, Runnable action) {
        var startedAt = start(logger, label);

        try {
            action.run();
            finish(logger, label, startedAt);
        } catch (RuntimeException | Error e) {
            fail(logger, label, startedAt, e);
            throw e;
        }
    }

    public static long start(Logger logger, String label) {
        logger.info("{} START {}", PREFIX, label);
        return System.nanoTime();
    }

    public static void finish(Logger logger, String label, long startedAtNanos) {
        logger.info("{} DONE {} in {} ms", PREFIX, label, elapsedMillis(startedAtNanos));
    }

    public static void fail(Logger logger, String label, long startedAtNanos, Throwable throwable) {
        logger.error("{} FAILED {} after {} ms", PREFIX, label, elapsedMillis(startedAtNanos), throwable);
    }

    public static long elapsedMillis(long startedAtNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAtNanos);
    }
}

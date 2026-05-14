package com.blib.mod.client.render.debug;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;

@ApiStatus.Internal
public final class PathfindingDebugState {

    public static final PathfindingDebugState INSTANCE = new PathfindingDebugState();

    private static final int TIMING_HISTORY_SIZE = 5;

    private @Nullable S2CPathfindingNavDebugPayload latestPayload;

    private final long[] timingHistoryNanos = new long[TIMING_HISTORY_SIZE];

    private int timingCount;

    private int timingIndex;

    private int lastSeenComputeTick = -1;

    private int lastEntityId = -1;

    private PathfindingDebugState() {}

    public void update(S2CPathfindingNavDebugPayload payload) {
        if (payload.entityId() != lastEntityId) {
            clearTiming();
            lastEntityId = payload.entityId();
        }

        latestPayload = payload;

        var computeTick = payload.lastPathComputeTick();
        if (computeTick != lastSeenComputeTick && payload.lastPathComputeNanos() > 0) {
            lastSeenComputeTick = computeTick;
            timingHistoryNanos[timingIndex] = payload.lastPathComputeNanos();
            timingIndex = (timingIndex + 1) % TIMING_HISTORY_SIZE;

            if (timingCount < TIMING_HISTORY_SIZE) {
                timingCount++;
            }
        }
    }

    public @Nullable S2CPathfindingNavDebugPayload latestPayload() {
        return latestPayload;
    }

    public List<Long> timingHistoryNanos() {
        if (timingCount == 0) {
            return List.of();
        }

        var history = new ArrayList<Long>(timingCount);
        var oldest = (timingCount < TIMING_HISTORY_SIZE) ? 0 : timingIndex;
        for (var i = 0; i < timingCount; i++) {
            history.add(timingHistoryNanos[(oldest + i) % TIMING_HISTORY_SIZE]);
        }
        return history;
    }

    public void clear() {
        latestPayload = null;
        lastEntityId = -1;
        clearTiming();
    }

    private void clearTiming() {
        timingCount = 0;
        timingIndex = 0;
        lastSeenComputeTick = -1;
    }
}

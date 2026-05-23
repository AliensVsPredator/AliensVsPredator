package com.blib.mod.client.render.debug;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

@ApiStatus.Internal
public final class PathfindingDebugState {

    public static final PathfindingDebugState INSTANCE = new PathfindingDebugState();

    private static final int TIMING_HISTORY_SIZE = 5;

    private @Nullable S2CPathfindingNavDebugPayload latestPayload;

    private @Nullable S2CPathfindingSearchDebugPayload latestSearchPayload;

    private final Map<Integer, S2CPathfindingSearchDebugPayload> searchPayloadsByEntityId = new ConcurrentHashMap<>();

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

    public void updateSearch(S2CPathfindingSearchDebugPayload payload) {
        latestSearchPayload = payload;
        if (payload.nodes().isEmpty() && payload.corridorKeys().isEmpty()) {
            searchPayloadsByEntityId.remove(payload.entityId());
        } else {
            searchPayloadsByEntityId.put(payload.entityId(), payload);
        }
    }

    public @Nullable S2CPathfindingSearchDebugPayload latestSearchPayload() {
        return latestSearchPayload;
    }

    public List<S2CPathfindingSearchDebugPayload> searchPayloads() {
        return List.copyOf(searchPayloadsByEntityId.values());
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
        latestSearchPayload = null;
        searchPayloadsByEntityId.clear();
        lastEntityId = -1;
        clearTiming();
    }

    private void clearTiming() {
        timingCount = 0;
        timingIndex = 0;
        lastSeenComputeTick = -1;
    }
}

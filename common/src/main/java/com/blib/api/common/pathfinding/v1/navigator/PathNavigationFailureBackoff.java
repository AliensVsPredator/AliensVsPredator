package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Owns failed-path cooldown timing and exponential failure backoff.
 */
final class PathNavigationFailureBackoff {

    private static final int BASE_FAILURE_COOLDOWN = 10;

    private static final int MAX_FAILURE_COOLDOWN = 200;

    private static final double FAILURE_START_RESET_DISTANCE_SQUARED = 4.0;

    private static final long NANOS_PER_TICK = 50_000_000L;

    private final LevelReader level;

    private final long createdNanos = System.nanoTime();

    PathNavigationFailureBackoff(LevelReader level) {
        this.level = level;
    }

    boolean isInFailureCooldown(
        PathNavigationStateComponent state,
        BlockPos entityStart
    ) {
        if (state.consecutiveFailures == 0) {
            return false;
        }

        if (hasMovedAwayFromFailedStart(state, entityStart)) {
            resetFailureCooldown(state);
            return false;
        }

        return cooldownClock() - state.lastFailureTick < state.failureCooldownTicks;
    }

    void recordFailure(PathNavigationStateComponent state, BlockPos entityStart) {
        state.consecutiveFailures++;
        state.lastFailureEntityStart = entityStart;
        state.lastFailureTick = cooldownClock();
        var shift = Math.min(state.consecutiveFailures - 1, 30);
        state.failureCooldownTicks = Math.min(BASE_FAILURE_COOLDOWN * (1 << shift), MAX_FAILURE_COOLDOWN);
    }

    void resetFailureCooldown(PathNavigationStateComponent state) {
        state.consecutiveFailures = 0;
        state.failureCooldownTicks = 0;
        state.lastFailureEntityStart = null;
    }

    long cooldownClock() {
        if (level instanceof Level concreteLevel) {
            return concreteLevel.getGameTime();
        }

        return (System.nanoTime() - createdNanos) / NANOS_PER_TICK;
    }

    private boolean hasMovedAwayFromFailedStart(PathNavigationStateComponent state, BlockPos entityStart) {
        return state.lastFailureEntityStart != null
            && entityStart.distSqr(state.lastFailureEntityStart) >= FAILURE_START_RESET_DISTANCE_SQUARED;
    }
}

package com.blib.mod.client.render.goap.model;

import org.jetbrains.annotations.Nullable;

import com.blib.mod.client.render.goap.constant.GOAPDebugHUDConstants;

public record DisplayPlan(
    GOAPPlanDebugData plan,
    @Nullable String terminalState,
    long ghostStartTime
) {

    private static final long GHOST_DURATION_MS = 2000;

    public boolean isGhost() {
        return terminalState != null;
    }

    public boolean isExpired(long now) {
        return isGhost() && (now - ghostStartTime) >= GHOST_DURATION_MS;
    }

    public int getColor() {
        if (!isGhost()) {
            return GOAPDebugHUDConstants.TEXT_COLOR;
        }

        return isFailedState(terminalState)
            ? GOAPDebugHUDConstants.FAILED_COLOR
            : GOAPDebugHUDConstants.COMPLETED_COLOR;
    }

    private static boolean isFailedState(String planState) {
        return "ABORTED".equals(planState) || "INVALID".equals(planState);
    }
}

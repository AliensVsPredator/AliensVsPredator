package com.blib.engine.input.router.coord;

import org.jetbrains.annotations.ApiStatus;

/**
 * Raw (physical) screen pixel coordinate. Wraps a {@code double} so a bare {@code int}/{@code double} can't be passed
 * where a logical-px is expected and vice versa — the prior codebase silently propagated coordinate-space mismatches
 * because every position parameter was just {@code double}. Used by {@link com.blib.engine.input.router.InputEvent} to
 * keep the original cursor position alongside the workspace's logical coord (post-{@code 0.375f} scale).
 *
 * @see LogicalPx
 */
@ApiStatus.Internal
public record RawPx(double value) {

    public static RawPx of(double v) {
        return new RawPx(v);
    }

    public LogicalPx toLogical(double scale) {
        return new LogicalPx(value / scale);
    }
}

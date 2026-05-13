package com.blib.engine.input.router.coord;

import org.jetbrains.annotations.ApiStatus;

/**
 * Logical pixel coordinate — the workspace's coord system after the global {@code SCALE = 0.375f} divide. Panels,
 * dialogs, popups and menus all work in this space; {@link RawPx} is only kept on
 * {@link com.blib.engine.input.router.InputEvent} for sub-tools that need to talk to vanilla coords (e.g. the cursor
 * ray code that consumes window-pixel positions).
 *
 * @see RawPx
 */
@ApiStatus.Internal
public record LogicalPx(double value) {

    public static LogicalPx of(double v) {
        return new LogicalPx(v);
    }

    public RawPx toRaw(double scale) {
        return new RawPx(value * scale);
    }

    public int intValue() {
        return (int) value;
    }
}

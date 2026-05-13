package com.blib.engine.input.router.coord;

import org.jetbrains.annotations.ApiStatus;

/**
 * Pixel coordinate relative to a specific panel's content area. Used by panel rendering and hit-testing so panel-local
 * math can't be accidentally mixed with workspace-global coords.
 */
@ApiStatus.Internal
public record PanelPx(double value) {

    public static PanelPx of(double v) {
        return new PanelPx(v);
    }

    public LogicalPx toLogical(double panelOrigin) {
        return new LogicalPx(value + panelOrigin);
    }
}

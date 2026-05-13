package com.blib.engine.input.router.coord;

import org.jetbrains.annotations.ApiStatus;

/**
 * Pixel coordinate relative to the viewport panel — i.e. logical-px minus the panel's top-left. Used by gizmo-pick and
 * wrapped-screen coordinate remapping so the two spaces (viewport vs. workspace) don't get conflated in method
 * signatures.
 */
@ApiStatus.Internal
public record ViewportPx(double value) {

    public static ViewportPx of(double v) {
        return new ViewportPx(v);
    }

    public LogicalPx toLogical(double viewportOrigin) {
        return new LogicalPx(value + viewportOrigin);
    }
}

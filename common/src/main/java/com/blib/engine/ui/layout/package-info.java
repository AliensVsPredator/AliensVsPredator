/**
 * Immediate-mode layout primitives for BLib engine panels.
 * <p>
 * These classes are intentionally small: panels keep their domain rendering, while shared rect math, clipped text,
 * raw-scissored scroll viewports, and simple vertical flow live here. New engine UI should route parent-relative sizing
 * and overflow behavior through this package instead of hand-writing cursor math in each panel.
 */
@org.jetbrains.annotations.ApiStatus.Internal
package com.blib.engine.ui.layout;

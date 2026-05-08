package com.blib.api.client.render.v1.item;

/**
 * Which on-screen gizmo (if any) the {@link BLibGeoBoneItemRenderer} should draw and capture clicks for.
 * Toggled via {@code /blib transform-tune debug gizmo translate|rotate|off}; only one type is shown at a
 * time to keep the visual uncluttered.
 */
public enum BLibGizmoMode {

    /** No gizmo rendered, no clicks captured. */
    OFF,

    /** Three axis arrows (red/green/blue = X/Y/Z) at the bone pivot. Drag to translate. */
    TRANSLATE,

    /** Three axis rings (red/green/blue = around X/Y/Z) at the bone pivot. Drag to rotate. */
    ROTATE
}

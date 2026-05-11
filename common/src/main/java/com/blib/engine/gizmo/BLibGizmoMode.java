package com.blib.engine.gizmo;

/**
 * Which on-screen gizmo (if any) the {@link com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer} should draw and
 * capture clicks for. Toggled via {@code /blib transform-tune debug gizmo translate|rotate|scale|off}; only one type is
 * shown at a time to keep the visual uncluttered.
 */
public enum BLibGizmoMode {

    /** No gizmo rendered, no clicks captured. */
    OFF,

    /** Three axis arrows (red/green/blue = X/Y/Z) at the bone pivot. Drag to translate. */
    TRANSLATE,

    /** Three axis rings (red/green/blue = around X/Y/Z) at the bone pivot. Drag to rotate. */
    ROTATE,

    /**
     * A single white shaft pointing +Y with a wireframe cube at the tip. Drag the handle along the line direction (up =
     * scale up, down = scale down) — applies a uniform delta to all three scale components.
     * {@link com.blib.api.client.render.v1.BLibTransform} stores scale as {@code Vector3f} but the user-facing API
     * treats it as a uniform scalar, so a 1D handle is the honest design.
     */
    SCALE
}

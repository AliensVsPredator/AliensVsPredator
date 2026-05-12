package com.blib.engine.modeler.gizmo;

/**
 * Active gizmo overlay in the modeler viewport. Switched via the viewport toolbar or T / R / S hotkeys (Esc → OFF).
 * Distinct from {@code BLibGizmoMode} because the modeler's scale handle is a 6-face resize (asymmetric per-face), not
 * the item-tuner's 1D uniform-scale handle.
 */
public enum ModelerGizmoMode {

    /** No gizmo rendered; LMB falls through to plain cube selection. */
    OFF,

    /** Three axis arrows at the selected cube's pivot in cube-local frame. Drag = translate {@code cube.origin}. */
    TRANSLATE,

    /** Three axis rings at the selected cube's pivot in cube-local frame. Drag = rotate {@code cube.rotation}. */
    ROTATE,

    /**
     * Six face-handle arrows (±X / ±Y / ±Z) on the selected cube's faces in cube-local frame. Drag a MAX face = grow
     * {@code cube.size} along that axis; drag a MIN face = shift {@code cube.origin} inward and grow {@code cube.size}
     * outward.
     */
    RESIZE
}

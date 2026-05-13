package com.blib.engine.modeler.gizmo;

/**
 * Active gizmo overlay in the modeler viewport. Switched via the viewport toolbar or T / R / S / P hotkeys (Esc → OFF).
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
    RESIZE,

    /**
     * Three axis arrows at the selected cube's pivot in cube-local frame. Drag = translate {@code cube.pivot}. The
     * cube's rendered geometry stays put when rotation is identity; with a non-zero authored rotation, the cube swings
     * around the new pivot (which is the desired effect — the user is repositioning the rotation center).
     */
    PIVOT,

    /**
     * Three axis arrows that drive uniform scale. Only meaningful for bones (mutates {@code bone.scale}) and for the
     * item-transform shim bone the preview viewport uses. Cubes don't have a scale field — SCALE is a no-op against a
     * cube selection. Drag along any axis applies a uniform scale factor proportional to cursor travel along that axis;
     * the same drag motion produces the same factor regardless of which axis ring the user grabbed.
     */
    SCALE
}

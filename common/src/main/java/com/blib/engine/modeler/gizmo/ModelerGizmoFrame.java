package com.blib.engine.modeler.gizmo;

import org.jetbrains.annotations.ApiStatus;

/**
 * Reference frame the modeler's translate / pivot gizmo handles are oriented in. Switched via the viewport toolbar
 * dropdown — affects only {@link ModelerGizmoMode#TRANSLATE} and {@link ModelerGizmoMode#PIVOT}; rotation rings and
 * resize face handles always stay in the cube's local frame (their semantics depend on cube axes).
 * <p>
 * Designed for extension — additional framings (parent-bone-relative, normal-aligned, etc.) can be appended without
 * touching the call sites that switch on this enum, since each frame defines its own axis convention.
 */
@ApiStatus.Internal
public enum ModelerGizmoFrame {

    /**
     * Cube's post-rotation axes. Dragging the green arrow moves the cube along the cube's authored Y direction, even if
     * the cube has been rotated relative to the scene. Default; matches every other modeler / Blender / Blockbench
     * gizmo when "Local" is selected.
     */
    LOCAL("Local"),

    /**
     * World axes. Dragging the green arrow always moves the cube along the scene's world Y, regardless of any bone or
     * cube rotations. Drag math inverse-transforms the world-space delta through the cumulative rotation chain so the
     * stored cube-local fields produce the expected world motion.
     */
    GLOBAL("Global");

    private final String label;

    ModelerGizmoFrame(String label) {
        this.label = label;
    }

    /** Display label shown on the toolbar frame button. */
    public String label() {
        return label;
    }

    /** Next frame in declaration order; wraps. Used by the toolbar button's cycle-on-click behavior. */
    public ModelerGizmoFrame next() {
        var values = values();
        return values[(ordinal() + 1) % values.length];
    }
}

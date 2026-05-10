package com.blib.engine.entityselection;

import org.jetbrains.annotations.ApiStatus;

/**
 * Active manipulation tool for the engine workspace's entity selection. Mirrors
 * {@link com.blib.engine.blockselection.BlockSelection.GizmoMode} for the parallel block-volume case but lives in its
 * own enum because entities only support a subset of operations: there's no entity-level analogue to MOVE_BLOCKS.
 * <p>
 * Driven by the workspace's T / S hotkeys when the active selection is an
 * {@link com.blib.engine.selection.EntitySelectable}; the same hotkeys still drive
 * {@link com.blib.engine.blockselection.BlockSelection.GizmoMode} when no entity is selected.
 * <p>
 * State is workspace-session-scoped — defaults to {@link #TRANSLATE} since that's the operation users reach for first
 * after selecting an entity. Cleared by the workspace's {@code removed()} alongside the gizmo state classes.
 */
@ApiStatus.Internal
public enum EntityGizmoMode {

    TRANSLATE,
    SCALE;

    private static EntityGizmoMode active = TRANSLATE;

    public static EntityGizmoMode get() {
        return active;
    }

    public static void set(EntityGizmoMode mode) {
        active = mode == null ? TRANSLATE : mode;
    }
}

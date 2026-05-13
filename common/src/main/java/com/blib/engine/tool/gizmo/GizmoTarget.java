package com.blib.engine.tool.gizmo;

import org.jetbrains.annotations.ApiStatus;

/**
 * Abstracts "the value being manipulated by a gizmo". A {@link Gizmo} writes its drag delta into a target's setter and
 * the target's {@link #commit} method decides what happens on release (network packet, undo entry, local mutation,
 * etc.). Lets a single {@code AxisTranslateGizmo<Vec3>} drive entities, block volumes, modeler cubes, or item
 * transforms without each having its own gizmo class.
 *
 * @param <V> the value type — typically a vector or a numeric scalar
 */
@ApiStatus.Internal
public interface GizmoTarget<V> {

    /** Snapshot the live value at drag-begin. The gizmo holds onto this so drag deltas are relative to the start. */
    V read();

    /** Write a ghost / preview value during drag. Implementations choose whether this also mutates the live world. */
    void preview(V value);

    /**
     * Commit the final drag value. Called once on release if the value changed; implementations decide whether to fire
     * a network packet, push to undo history, or update local state.
     */
    void commit(V before, V after);

    /** Roll back any preview state. Called when a drag ends with no net change, or on cancel. */
    default void cancel() {}
}

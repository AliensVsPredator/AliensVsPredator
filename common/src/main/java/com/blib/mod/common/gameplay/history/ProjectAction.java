package com.blib.mod.common.gameplay.history;

import org.jetbrains.annotations.ApiStatus;

/**
 * Engine-mode action that mutates project-scoped on-disk JSON (pools, tags) rather than world state. Dimension-agnostic
 * — the player's current level doesn't constrain which {@code ProjectAction} entries can be undone (a tag edit doesn't
 * "live" in any dimension).
 * <p>
 * Currently {@code ProjectJsonEdit} is the v1 variant; faction edits live under a separate {@code FactionEdit}
 * top-level record alongside this interface.
 */
@ApiStatus.Internal
public sealed interface ProjectAction extends EditorAction permits ProjectJsonEdit {

    String projectName();
}

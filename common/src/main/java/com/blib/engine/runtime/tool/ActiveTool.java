package com.blib.engine.runtime.tool;

import org.jetbrains.annotations.ApiStatus;

/**
 * The set of mutually-exclusive editor tools. At most one is "armed" at any time — the viewport's primary-click action
 * is dispatched based on the active tool, and per-tool state (jigsaw piece on cursor, entity-to-spawn, paint target,
 * AABB corners) auto-disarms when the user switches away.
 * <p>
 * Replaces the prior pattern where each tool's static singleton had to know about the others and call their
 * {@code clear()} methods directly (an implicit O(N²) graph). Each tool now reacts to {@link ToolChangedEvent}
 * published by {@link ToolStateMachine#activate}.
 */
@ApiStatus.Internal
public enum ActiveTool {

    /** Default. LMB picks; no special preview. */
    SELECT,

    /** AABB volume selection mode (corner pick, gizmo manipulation). */
    BLOCK_VOLUME,

    /** Jigsaw piece placement under cursor; LMB commits placement. */
    JIGSAW_PLACE,

    /** Entity-spawn placement under cursor; LMB commits spawn. */
    ENTITY_SPAWN,

    /** Territory claim painting; LMB-drag paints claims with the active faction colour. */
    CLAIM_PAINT,

    /** Modeler mode (separate viewport, bone/cube selection + transforms). */
    MODELER
}

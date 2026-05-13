package com.blib.engine.domain.selection.picking.event;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.Selection;

/**
 * Published by {@code SelectionManager} when the workspace's current selection is replaced. Subscribers (inspector
 * panels, renderers, highlight passes) can react to the transition instead of polling
 * {@code SelectionManager.current()} every frame.
 * <p>
 * Step 8 of the engine architecture refactor — emits the event from every selection-mutation entry point. Panels can
 * subscribe at construction and recompute cached display state only when the selection actually changes.
 *
 * @param previous the prior selection (immutable snapshot); {@link Selection#empty} if nothing was selected
 * @param current  the new selection (immutable snapshot)
 */
@ApiStatus.Internal
public record SelectionChangedEvent(
    Selection previous,
    Selection current
) {}

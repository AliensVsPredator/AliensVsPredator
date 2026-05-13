package com.blib.engine.domain.selection.picking;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.event.SelectionChangedEvent;
import com.blib.engine.runtime.EventBus;

/**
 * Mutable singleton holding the workspace's current {@link Selection}. Replaces the per-session
 * {@code EngineSession.selectedEntity} field — selection now spans entities, blocks, limbs, and any other
 * {@link Selectable} types without coupling to {@code LivingEntity} specifically.
 * <p>
 * Reads return a snapshot; concurrent writes from other call sites can't tear partway through an inspector render since
 * {@link Selection} is immutable. Cleared on workspace close.
 * <p>
 * Publishes {@link SelectionChangedEvent} on every successful state change. Subscribers can react to the transition
 * instead of polling {@link #current} every frame — Step 8 of the engine architecture refactor introduces the event to
 * give panels and renderers a notification path. Polling readers continue to work unchanged.
 */
@ApiStatus.Internal
public final class SelectionManager {

    private static Selection current = Selection.empty();

    private SelectionManager() {}

    /**
     * Returns the current selection, pruning stale items first. A selection containing a since-unloaded entity (or
     * other no-longer-valid target) is rebuilt without the stale entries — the inspector won't try to render dead
     * state, and the highlight renderer doesn't draw a zero-volume AABB at the world origin.
     */
    public static Selection current() {
        if (current.isEmpty()) {
            return current;
        }
        var hasStale = false;
        for (var item : current.items()) {
            if (!item.isValid()) {
                hasStale = true;
                break;
            }
        }
        if (!hasStale) {
            return current;
        }
        var previous = current;
        var pruned = current.items().stream().filter(Selectable::isValid).toList();
        current = pruned.isEmpty() ? Selection.empty() : new Selection(pruned);
        // Pruning is a state change too — subscribers that mirror selection state need the heads-up.
        EventBus.get().publish(new SelectionChangedEvent(previous, current));
        return current;
    }

    /** Replace the selection wholesale. {@code null} maps to empty. */
    public static void replace(Selection selection) {
        var next = selection == null ? Selection.empty() : selection;
        if (next == current) {
            return;
        }
        var previous = current;
        current = next;
        EventBus.get().publish(new SelectionChangedEvent(previous, current));
    }

    /** Convenience: replace with a single-item selection, or clear if {@code null}. */
    public static void selectSingle(Selectable selectable) {
        replace(selectable == null ? Selection.empty() : Selection.single(selectable));
    }

    public static void clear() {
        if (current.isEmpty()) {
            return;
        }
        var previous = current;
        current = Selection.empty();
        EventBus.get().publish(new SelectionChangedEvent(previous, current));
    }
}

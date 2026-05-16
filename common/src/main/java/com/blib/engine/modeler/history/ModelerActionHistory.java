package com.blib.engine.modeler.history;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.blib.engine.history.api.HistoryService;
import com.blib.engine.modeler.ModelerScene;
import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Bounded client-side undo/redo store for modeler gestures. Mirrors the shape of the server-side
 * {@code com.blib.mod.common.gameplay.history.ActionHistory} but lives purely on the client: the modeler scene is
 * heap-only (no server sync), so reverting a gesture is a local field-write rather than a network round-trip.
 * <p>
 * The {@link com.blib.engine.ui.panel.action.ActionStackPanel} reads this store via {@link #descriptors} /
 * {@link #undoCursor} when the active workspace layout contains client-local authoring panels (modeler / texture) —
 * otherwise it shows the server-synced history. Cleared on
 * {@link com.blib.engine.modeler.ModelerSceneLoader#loadFromFile model load} since the new scene's bones / cubes are
 * different heap instances that the existing actions can't address.
 */
@ApiStatus.Internal
public final class ModelerActionHistory {

    private static final int MAX_ENTRIES = 128;

    private static final Deque<ModelerAction> undoStack = new ArrayDeque<>();

    private static final Deque<ModelerAction> redoStack = new ArrayDeque<>();

    private ModelerActionHistory() {}

    /** Push a freshly-performed action. Clears redo (standard editor semantics) and trims to {@link #MAX_ENTRIES}. */
    public static synchronized void push(ModelerAction action) {
        redoStack.clear();
        undoStack.addFirst(action);
        while (undoStack.size() > MAX_ENTRIES) {
            undoStack.removeLast();
        }
        bumpSceneRevision(action);
    }

    /** Pop the most recent undo entry, invoke its undo(), and stash it on the redo stack. */
    public static synchronized boolean undo() {
        var action = undoStack.pollFirst();
        if (action == null) {
            return false;
        }
        action.undo();
        redoStack.addFirst(action);
        bumpSceneRevision(action);
        return true;
    }

    /** Mirror of {@link #undo} for the redo direction. */
    public static synchronized boolean redo() {
        var action = redoStack.pollFirst();
        if (action == null) {
            return false;
        }
        action.redo();
        undoStack.addFirst(action);
        bumpSceneRevision(action);
        return true;
    }

    public static synchronized void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public static synchronized int undoSize() {
        return undoStack.size();
    }

    public static synchronized int redoSize() {
        return redoStack.size();
    }

    /**
     * Flatten both stacks into the same descriptor layout the server-side history uses: undo entries newest-first
     * followed by redo entries newest-first, so the panel can render a single continuous list with {@link #undoCursor}
     * marking the boundary between the two halves.
     */
    public static synchronized List<ActionDescriptor> descriptors() {
        var out = new ArrayList<ActionDescriptor>(undoStack.size() + redoStack.size());
        for (var action : undoStack) {
            out.add(action.toDescriptor());
        }
        for (var action : redoStack) {
            out.add(action.toDescriptor());
        }
        return out;
    }

    public static synchronized int undoCursor() {
        return undoStack.size();
    }

    /**
     * Read-only adapter that exposes this static store as a {@link HistoryService} — lets UI panels stay agnostic to
     * which history backend they're reading.
     */
    public static HistoryService asService() {
        return SERVICE;
    }

    private static final HistoryService SERVICE = new HistoryService() {

        @Override
        public List<ActionDescriptor> entries() {
            return descriptors();
        }

        @Override
        public int undoCursor() {
            return ModelerActionHistory.undoCursor();
        }
    };

    private static void bumpSceneRevision(ModelerAction action) {
        if (action.affectsModelScene()) {
            ModelerScene.get().bumpRevision();
        }
    }
}

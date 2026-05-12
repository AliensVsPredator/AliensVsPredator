package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Bounded server-side undo/redo store for engine-mode actions. Holds a generalized stack of {@link EditorAction}
 * instances covering blocks, entities, chunk claims, and project metadata.
 * <p>
 * Two deques: {@code undoStack} (newest at head) holds actions that {@link #undo} pops; {@code redoStack} holds actions
 * popped by undo, available for {@link #redo}. Pushing a fresh action clears the redo stack — standard editor
 * semantics, confirmed with the user during plan review.
 * <p>
 * Dual cap: at most {@link #MAX_ENTRIES} entries per stack, and at most {@link #MAX_BYTES} total retained bytes (sum of
 * {@link EditorAction#estimatedBytes} over both stacks). On push, oldest entries are evicted until both caps are
 * satisfied. A single action that exceeds the byte cap on its own is admitted (and everything else evicted) — the
 * gesture just happened, refusing to make it undoable would be worse than blowing the cap.
 * <p>
 * Dimension filter on undo: {@link WorldAction} entries only match when the player's current level matches the action's
 * stored dimension. Cross-dimension placements stay in the stack until the player returns. Future {@code ProjectAction}
 * entries match unconditionally.
 */
@ApiStatus.Internal
public final class ActionHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHistory.class);

    private static final int MAX_ENTRIES = 64;

    private static final long MAX_BYTES = 128L * 1024L * 1024L;

    private static final Deque<EditorAction> undoStack = new ArrayDeque<>();

    private static final Deque<EditorAction> redoStack = new ArrayDeque<>();

    /** Listener fires after every mutation so the S2C history sync can broadcast a fresh descriptor list. */
    @Nullable
    private static Runnable changeListener;

    private ActionHistory() {}

    public static synchronized void setChangeListener(@Nullable Runnable listener) {
        changeListener = listener;
    }

    /**
     * Push a freshly-performed action. Clears the redo stack (standard editor semantics — once you do something new
     * after an undo, the previously-undone branch is gone) and trims the undo stack to the dual cap.
     */
    public static synchronized void push(EditorAction action) {
        redoStack.clear();
        undoStack.addFirst(action);
        trim();
        notifyChanged();
    }

    /**
     * Pop the most recent undo entry whose dimension matches {@code level} (or any {@code ProjectAction}), call its
     * {@code revert}, move it to the redo stack, and return it. Returns {@code null} if nothing matches.
     */
    @Nullable
    public static synchronized EditorAction undo(MinecraftServer server, ServerLevel level) {
        var match = findMatching(undoStack, level.dimension());
        if (match == null) {
            return null;
        }
        match.revert(server);
        redoStack.addFirst(match);
        trim();
        notifyChanged();
        return match;
    }

    /** Mirror of {@link #undo} for the redo direction. */
    @Nullable
    public static synchronized EditorAction redo(MinecraftServer server, ServerLevel level) {
        var match = findMatching(redoStack, level.dimension());
        if (match == null) {
            return null;
        }
        match.redo(server);
        undoStack.addFirst(match);
        trim();
        notifyChanged();
        return match;
    }

    /** Wipe both stacks. Called on server-stop so a fresh session starts clean. */
    public static synchronized void clear() {
        undoStack.clear();
        redoStack.clear();
        notifyChanged();
    }

    /** Snapshot of the undo stack newest-first. Used by the sync payload to mirror state to clients. */
    public static synchronized List<EditorAction> undoSnapshot() {
        return new ArrayList<>(undoStack);
    }

    /** Snapshot of the redo stack newest-first. */
    public static synchronized List<EditorAction> redoSnapshot() {
        return new ArrayList<>(redoStack);
    }

    public static synchronized int undoSize() {
        return undoStack.size();
    }

    public static synchronized int redoSize() {
        return redoStack.size();
    }

    @Nullable
    private static EditorAction findMatching(Deque<EditorAction> stack, ResourceKey<Level> levelDim) {
        Iterator<EditorAction> iter = stack.iterator();
        while (iter.hasNext()) {
            var action = iter.next();
            if (matches(action, levelDim)) {
                iter.remove();
                return action;
            }
        }
        return null;
    }

    private static boolean matches(EditorAction action, ResourceKey<Level> levelDim) {
        if (action instanceof WorldAction world) {
            return world.dimension().equals(levelDim);
        }
        // Project actions (and any future dimension-agnostic kind) match regardless of player's current level.
        return true;
    }

    /**
     * Evict oldest entries from both stacks until size and byte caps are satisfied. A single oversize action stays (and
     * everything else gets evicted) — see class doc.
     */
    private static void trim() {
        while (undoStack.size() > MAX_ENTRIES) {
            undoStack.removeLast();
        }
        while (redoStack.size() > MAX_ENTRIES) {
            redoStack.removeLast();
        }
        long bytes = totalBytes();
        if (bytes <= MAX_BYTES) {
            return;
        }
        // Drop oldest entries across both stacks until under cap. Pop redo first since it's the "speculative" half.
        while (bytes > MAX_BYTES && !redoStack.isEmpty()) {
            var dropped = redoStack.pollLast();
            if (dropped != null) {
                bytes -= dropped.estimatedBytes();
            }
        }
        while (bytes > MAX_BYTES && undoStack.size() > 1) {
            var dropped = undoStack.pollLast();
            if (dropped != null) {
                bytes -= dropped.estimatedBytes();
            }
        }
        if (bytes > MAX_BYTES) {
            LOGGER.warn(
                "[BLib] ActionHistory: single action exceeds {} byte cap ({} bytes retained)",
                MAX_BYTES,
                bytes
            );
        }
    }

    private static long totalBytes() {
        long bytes = 0L;
        for (var a : undoStack) {
            bytes += a.estimatedBytes();
        }
        for (var a : redoStack) {
            bytes += a.estimatedBytes();
        }
        return bytes;
    }

    private static void notifyChanged() {
        if (changeListener != null) {
            try {
                changeListener.run();
            } catch (Throwable t) {
                LOGGER.warn("[BLib] ActionHistory: change listener threw", t);
            }
        }
    }
}

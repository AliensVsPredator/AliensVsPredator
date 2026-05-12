package com.blib.engine.history;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Client-side mirror of the server's {@code ActionHistory}, fed by {@code S2CActionHistorySyncPayload}. The
 * {@link com.blib.engine.ui.ActionStackPanel ActionStackPanel} reads this singleton to render its rows.
 * <p>
 * {@link #entries} is the concatenation of the server's undo stack (newest first) and the redo stack (oldest first);
 * indices {@code [0, undoCursor)} are undoable, {@code [undoCursor, entries.size())} are redoable. Cleared on
 * engine-mode exit so the panel doesn't ghost stale data into the next session.
 */
@ApiStatus.Internal
public final class ClientActionHistory {

    public static final ClientActionHistory INSTANCE = new ClientActionHistory();

    private volatile List<ActionDescriptor> entries = List.of();

    private volatile int undoCursor = 0;

    private ClientActionHistory() {}

    public void update(List<ActionDescriptor> entries, int undoCursor) {
        this.entries = List.copyOf(entries);
        this.undoCursor = Math.max(0, Math.min(undoCursor, entries.size()));
    }

    public void clear() {
        this.entries = Collections.emptyList();
        this.undoCursor = 0;
    }

    public List<ActionDescriptor> entries() {
        return entries;
    }

    public int undoCursor() {
        return undoCursor;
    }
}

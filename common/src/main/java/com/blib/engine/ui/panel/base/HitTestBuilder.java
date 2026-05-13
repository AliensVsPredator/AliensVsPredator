package com.blib.engine.ui.panel.base;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Per-frame hit-rect collector. Replaces the ad-hoc {@code List<RowHit>} field + {@code rowHits.clear()} pattern that
 * every list-shaped panel repeats. The builder is reset by the panel's render method at the start of the frame; rows
 * call {@link #add} as they're drawn; click handlers iterate the captured hits and dispatch to their attached action.
 * <p>
 * Each hit carries an arbitrary payload {@code T} so callers don't have to maintain parallel arrays for "which entity
 * was at row index N". Generic instead of {@code Object} because most callers want a concrete type at the call site
 * (e.g. {@code HitTestBuilder<Entry>}).
 *
 * @param <T> payload type associated with each hit rect
 */
@ApiStatus.Internal
public final class HitTestBuilder<T> {

    private final List<Hit<T>> hits = new ArrayList<>();

    /** Clear the list. Call at the start of each render pass before adding row rects. */
    public void reset() {
        hits.clear();
    }

    public void add(int x, int y, int width, int height, T payload) {
        hits.add(new Hit<>(x, y, width, height, payload));
    }

    /** First hit whose rect contains the point, or {@code null}. Last-added-first iteration so overlapping wins. */
    public @Nullable T at(double mx, double my) {
        for (var i = hits.size() - 1; i >= 0; i--) {
            var h = hits.get(i);
            if (mx >= h.x && mx < h.x + h.width && my >= h.y && my < h.y + h.height) {
                return h.payload;
            }
        }
        return null;
    }

    /** Iterate every captured hit (for the panel that needs to render hover background or similar). */
    public void forEach(Consumer<Hit<T>> consumer) {
        hits.forEach(consumer);
    }

    public record Hit<T>(
        int x,
        int y,
        int width,
        int height,
        T payload
    ) {}
}

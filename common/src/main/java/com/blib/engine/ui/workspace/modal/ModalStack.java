package com.blib.engine.ui.workspace.modal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Ordered z-stack of named modals. Tags are registered up-front with a {@link BooleanSupplier} that reports each
 * modal's current open state; {@link #sync()} reconciles the stack against those suppliers each frame so callers do
 * <strong>not</strong> have to push/pop explicitly — they keep doing {@code this.fooDialog = new FooDialog(...)} /
 * {@code = null} and the stack figures it out by diff.
 * <p>
 * Newly-opened tags are pushed to the top of the stack; closed tags are removed. Re-opening pushes back to the top.
 * Render and input dispatch consult {@link #top()} so a child modal opened over a parent (e.g. a Confirm spawned by
 * Preferences) reliably renders and receives input on top.
 * <p>
 * This intentionally tracks <em>tags</em> rather than modal objects — callers already hold typed references in their
 * fields and just need the stack to answer "which one is on top?" without intermediate handles.
 */
@ApiStatus.Internal
public final class ModalStack {

    private final Map<String, BooleanSupplier> sources = new LinkedHashMap<>();

    private final Map<String, Boolean> last = new LinkedHashMap<>();

    private final List<String> order = new ArrayList<>();

    public void register(String tag, BooleanSupplier isOpen) {
        sources.put(tag, isOpen);
        last.put(tag, false);
    }

    /**
     * Reconcile the stack against the registered suppliers. Idempotent; safe to call repeatedly within a frame —
     * subsequent calls in the same frame are no-ops until a supplier transitions again.
     */
    public void sync() {
        for (var entry : sources.entrySet()) {
            var tag = entry.getKey();
            var openNow = entry.getValue().getAsBoolean();
            var openLast = last.getOrDefault(tag, false);
            if (openNow && !openLast) {
                order.remove(tag);
                order.add(tag);
            } else if (!openNow && openLast) {
                order.remove(tag);
            }
            last.put(tag, openNow);
        }
    }

    public boolean isAnyOpen() {
        for (var src : sources.values()) {
            if (src.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    public @Nullable String top() {
        sync();
        return order.isEmpty() ? null : order.get(order.size() - 1);
    }

    public List<String> order() {
        sync();
        return List.copyOf(order);
    }
}

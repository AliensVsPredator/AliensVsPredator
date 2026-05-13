package com.blib.engine.input.router;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered chain of {@link InputHandler}s. Handlers are dispatched front-to-back; the first to return
 * {@link InputClaim#CLAIMED} stops the chain. Wraps the conceptual priority "modals → popups → captures → dividers →
 * tabs → panels → workspace" that {@code EngineWorkspaceScreen.mouseClicked} previously inlined as a 360-line if-else
 * cascade.
 * <p>
 * Each engine workspace owns a router instance; handlers are registered at workspace construction (modal stack on top,
 * panel dispatch at the bottom, etc.). Tests can construct a router with a custom chain to verify priority semantics
 * without instantiating the full screen — that was previously impossible.
 */
@ApiStatus.Internal
public final class InputRouter {

    private final List<Entry> chain = new ArrayList<>();

    /** Register a handler at the given priority. Lower priority values run first. */
    public synchronized void register(int priority, InputHandler handler) {
        chain.add(new Entry(priority, handler));
        chain.sort((a, b) -> Integer.compare(a.priority, b.priority));
    }

    public synchronized void unregister(InputHandler handler) {
        chain.removeIf(e -> e.handler == handler);
    }

    /** Dispatch an event through the chain. Returns true if some handler claimed it. */
    public synchronized boolean dispatch(InputEvent event) {
        for (var entry : chain) {
            if (entry.handler.handle(event) == InputClaim.CLAIMED) {
                return true;
            }
        }
        return false;
    }

    /** Standard priority levels — engine workspace handlers register at these slots. */
    public static final class Priority {

        public static final int MODAL = 100;

        public static final int POPUP = 200;

        public static final int TOOL_DRAG = 300;

        public static final int CAPTURE = 400;

        public static final int DIVIDER = 500;

        public static final int TAB = 600;

        public static final int MENU_BAR = 700;

        public static final int PANEL = 800;

        public static final int WORKSPACE = 900;

        private Priority() {}
    }

    private record Entry(
        int priority,
        InputHandler handler
    ) {}
}

package com.blib.engine.runtime;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Synchronous, in-process event bus scoped to a single engine session. The current implementation is a static singleton
 * so existing static callers (tool singletons, panels, etc.) can publish/subscribe without threading an instance
 * through their call chains; {@link com.blib.engine.session.EngineMode#enter} registers a {@code scope.onClose} that
 * drains the subscriber map so listeners installed during one session don't leak into the next.
 * <p>
 * Step 9 of the engine architecture refactor promotes services to instances; at that point the bus moves into
 * {@code SessionScope} and the static accessor is deleted. The API shape here is forward-compatible.
 */
@ApiStatus.Internal
public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    private final Map<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    private EventBus() {}

    public static EventBus get() {
        return INSTANCE;
    }

    /**
     * Subscribe to events of the given type for the remainder of the active engine session. Handlers are dropped when
     * {@link #clear()} is called on session close. Re-subscribing on the next session is the caller's responsibility —
     * typically done from the singleton's {@code installToolListener()} hook invoked at session enter.
     */
    public <E> void subscribe(Class<E> type, Consumer<E> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Publish to every handler registered for the event's runtime type. Handlers fire in subscription order; an
     * exception in one handler is swallowed so a single broken listener doesn't cancel the rest of the broadcast.
     */
    @SuppressWarnings("unchecked")
    public <E> void publish(E event) {
        var list = handlers.get(event.getClass());
        if (list == null) {
            return;
        }
        for (var handler : list) {
            try {
                ((Consumer<E>) handler).accept(event);
            } catch (RuntimeException ignored) {
                // Swallow so a single faulty listener can't cancel the broadcast. Real fixes happen in the listener.
            }
        }
    }

    /** Drop every subscription. Wired to {@code EngineSessionScope.onClose} so the next session starts empty. */
    public void clear() {
        handlers.clear();
    }
}

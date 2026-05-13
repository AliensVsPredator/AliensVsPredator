package com.blib.engine.runtime;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.blib.engine.core.lifecycle.EngineSessionScope;

/**
 * Synchronous, in-process event bus owned by the active {@link EngineSessionScope}. A fresh instance is created on
 * every engine activation and registered in the scope's {@code ServiceContainer}, so subscribers from one session can't
 * leak into the next — the previous instance is dropped on scope close.
 * <p>
 * {@link #get()} routes through {@link EngineSessionHolder#current()} for callers (panels, tool singletons, mixin entry
 * points) that don't have a scope reference threaded in. It throws if no session is active — callers must gate on
 * engine state before publishing.
 */
@ApiStatus.Internal
public final class EventBus {

    private final HashMap<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    public EventBus() {}

    /**
     * The active session's bus. Looks up the instance via {@link EngineSessionHolder}, which is set by
     * {@code EngineMode.enter} before any subscribers are installed.
     *
     * @throws IllegalStateException if called outside an engine session
     */
    public static EventBus get() {
        var scope = EngineSessionHolder.current();
        if (scope == null) {
            throw new IllegalStateException("EventBus.get() called outside an engine session");
        }
        return scope.services().require(EventBus.class);
    }

    /**
     * Subscribe to events of the given type for the remainder of the active engine session. Handlers are dropped when
     * the bus instance is collected on session close. Re-subscribing on the next session is the caller's responsibility
     * — typically done from the tool singleton's {@code installToolListener()} hook invoked at session enter.
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
}

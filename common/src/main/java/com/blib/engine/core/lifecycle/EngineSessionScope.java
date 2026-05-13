package com.blib.engine.core.lifecycle;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Deque;

import com.blib.engine.runtime.ServiceContainer;

/**
 * Per-engine-session lifetime scope. Resources that should live exactly for the duration of one engine activation —
 * client-only caches, network mirrors, staging buffers — register a cleanup action on the active scope at construction
 * (or when first populated); the scope runs them in LIFO order on close.
 * <p>
 * Replaces the manual list of {@code .clear()} calls in {@code EngineMode.exit()}: adding a new session-scoped resource
 * means {@code scope.onClose(MyCache::clear)}, not editing the exit handler.
 * <p>
 * Also owns a {@link ServiceContainer} — Step 9 of the engine architecture refactor. As static singletons are migrated
 * to instance services, the new instances live here and are looked up by type via {@link #services()}.
 */
@ApiStatus.Internal
public final class EngineSessionScope {

    private final Deque<Runnable> closeActions = new ArrayDeque<>();

    private final ServiceContainer services = new ServiceContainer();

    private boolean closed;

    /**
     * Session-scoped service container. Services registered here are accessible to subsystems via
     * {@code EngineSessionHolder.current().services()}, and are auto-disposed when the scope closes (provided their
     * cleanup is registered with {@link #onClose}).
     */
    public ServiceContainer services() {
        return services;
    }

    /**
     * Register a cleanup action to run on {@link #close()}. Actions fire in reverse registration order so dependencies
     * unwind cleanly. Calling on a closed scope runs the action immediately.
     */
    public void onClose(Runnable action) {
        if (closed) {
            action.run();
            return;
        }
        closeActions.addFirst(action);
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        while (!closeActions.isEmpty()) {
            var a = closeActions.pollFirst();
            try {
                a.run();
            } catch (RuntimeException ignored) {
                // Swallow so a single failing cleanup doesn't prevent the rest from running. The engine session is
                // tearing down regardless; an exception here usually means the resource was already half-released
                // (e.g. cleared elsewhere).
            }
        }
        services.clear();
    }
}

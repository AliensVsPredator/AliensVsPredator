package com.blib.engine.runtime;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Type-keyed container of session-scoped services. The eventual destination of the engine's static singletons: each
 * service is constructed once at session enter, registered here, and looked up by type at use sites. Services that
 * register themselves with the session scope's {@code onClose} get disposed in LIFO order on session exit.
 * <p>
 * Step 9 of the engine architecture refactor introduces the container infrastructure so services can be migrated from
 * statics to instances one at a time. {@code EngineSessionHolder} (the single remaining static) lets mixin entry points
 * reach into the active session's container without threading instances through every static call.
 */
@ApiStatus.Internal
public final class ServiceContainer {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void put(Class<T> type, T instance) {
        if (services.containsKey(type)) {
            throw new IllegalStateException("Service already registered: " + type.getName());
        }
        services.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(Class<T> type) {
        return (T) services.get(type);
    }

    @SuppressWarnings("unchecked")
    public <T> T require(Class<T> type) {
        var instance = (T) services.get(type);
        if (instance == null) {
            throw new IllegalStateException("Service not registered: " + type.getName());
        }
        return instance;
    }

    public boolean has(Class<?> type) {
        return services.containsKey(type);
    }

    public void clear() {
        services.clear();
    }
}

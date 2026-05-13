package com.blib.engine.runtime;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.core.lifecycle.EngineSessionScope;

/**
 * The one static accessor that survives the engine's "kill statics" refactor. Required because mixin entry points
 * (which inject into Minecraft classes) live outside the engine's own call graph, so they can't have a session instance
 * passed in.
 * <p>
 * Step 9 of the engine architecture refactor: this holder + {@link ServiceContainer} (owned by the session scope)
 * replace the engine's per-feature static singletons. Each subsequent service migration removes one static class and
 * registers an instance with the session's container — accessible via {@link #current()} →
 * {@code .services().require(MyService.class)}.
 */
@ApiStatus.Internal
public final class EngineSessionHolder {

    private static volatile @Nullable EngineSessionScope current;

    private EngineSessionHolder() {}

    public static @Nullable EngineSessionScope current() {
        return current;
    }

    public static void set(@Nullable EngineSessionScope scope) {
        current = scope;
    }
}

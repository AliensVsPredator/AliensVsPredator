package com.blib.engine.platform;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.input.BLibKeyBindings;
import com.blib.engine.session.EngineNavigation;

/**
 * One-shot installer that wires the engine's loader-bound hooks through an {@link EnginePlatform}. Each loader's client
 * entry point ({@code BLibFabricClient}, {@code BLibNeoForgeClient}) constructs its platform implementation, then calls
 * {@link #install} exactly once during client init.
 * <p>
 * Centralising the call list here means new engine-side hooks land in one place — adding "register a chat overlay
 * renderer" used to mean editing six files (three Fabric + three NeoForge); now it's one new method on
 * {@link EnginePlatform} and one new {@link #install} line.
 */
@ApiStatus.Internal
public final class EngineBootstrap {

    private EngineBootstrap() {}

    public static void install(EnginePlatform platform) {
        platform.registerClientTickEnd(EngineNavigation::tick);
        platform.registerKeyBinding(BLibKeyBindings.TOGGLE_ENGINE);
    }
}

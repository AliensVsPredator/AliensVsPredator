package com.blib.engine.platform;

import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

/**
 * Loader-agnostic registration surface for the engine. Each Fabric / NeoForge module supplies one implementation that
 * adapts these generic hooks onto the loader's actual event-bus ceremony; the engine itself only calls these methods
 * and never reaches into loader-specific APIs.
 * <p>
 * Replaces the prior per-loader {@code BLibFabricEngineMode} / {@code BLibFabricKeyBindings} /
 * {@code BLibFabricGizmoPreview} (and NeoForge counterparts) — three near-identical pairs of 20-line files with no
 * shared abstraction. {@link EngineBootstrap#install} now drives all three registrations from a single common-side call
 * against this interface.
 */
@ApiStatus.Internal
public interface EnginePlatform {

    /** Run {@code handler} at the end of every client tick. */
    void registerClientTickEnd(Runnable handler);

    /** Register a vanilla {@link KeyMapping} with the loader so it shows up in the controls menu. */
    void registerKeyBinding(KeyMapping keyMapping);

    /** Run {@code renderer} after the vanilla HUD each frame, with {@code partialTick} delta. */
    void registerHudRenderer(HudRenderer renderer);
}

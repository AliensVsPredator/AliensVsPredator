package com.blib.api.client.event.v1;

/**
 * Fires after a {@link net.minecraft.client.gui.screens.Screen}'s {@code init()} method finishes — both on initial
 * activation and on window resize. Cross-loader: Fabric bridges this from {@code ScreenEvents.AFTER_INIT}, NeoForge
 * bridges from {@code ScreenEvent.Init.Post}. Use the {@link BLibScreenInitContext} to inspect the screen and add
 * widgets in a loader-agnostic way.
 * <p>
 * Server-side mod builds never fire this event (no screens on a dedicated server) — the loader bridges no-op the
 * registration in that case, so listeners just stay dormant.
 */
public interface BLibScreenInitEvent {

    void invoke(BLibScreenInitContext ctx);
}

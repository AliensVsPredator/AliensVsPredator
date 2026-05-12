package com.blib.api.client.event.v1;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

/**
 * Loader-agnostic surface passed to a {@link BLibScreenInitEvent} listener. Wraps the {@link Screen} being initialised
 * plus the loader's specific "add a widget to this screen" hook so listeners stay in common code without needing to
 * import Fabric's {@code Screens} or NeoForge's {@code ScreenEvent.Init.Post}.
 */
public interface BLibScreenInitContext {

    /** The screen vanilla just finished {@code init}-ing. {@code .width} / {@code .height} are valid public fields. */
    Screen screen();

    /**
     * Add a widget (button, slider, etc.) to the screen. Returns the same instance so callers can chain. The widget
     * lifetime is owned by the screen — it'll be re-created the next time {@code init} fires (e.g. on window resize),
     * so callers should re-add inside the listener rather than holding the reference across frames.
     */
    <W extends AbstractWidget> W addWidget(W widget);
}

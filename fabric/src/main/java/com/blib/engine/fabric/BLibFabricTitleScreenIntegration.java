package com.blib.engine.fabric;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screens.TitleScreen;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.BLibTitleScreenIntegration;

/**
 * Fabric wiring for the "Open BLib" button on the main menu. Hooks {@link ScreenEvents#AFTER_INIT} to add the button
 * after vanilla finishes laying out the TitleScreen widgets.
 */
@ApiStatus.Internal
public final class BLibFabricTitleScreenIntegration {

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof TitleScreen)) {
                return;
            }
            // Suppress when the engine is wrapping this TitleScreen as its menu-overlay backdrop — the engine init's
            // the wrapped screen, which re-fires AFTER_INIT, and we don't want a duplicate "Open BLib" button to
            // appear inside the viewport rect (the user would be trying to "open" while the engine is already open).
            if (client.screen instanceof com.blib.engine.ui.EngineWorkspaceScreen) {
                return;
            }
            // Bottom row, to the left of Options. Vanilla TitleScreen positions the bottom row at
            // (height/4 + 48 + 72 + 12); use the same y so we line up with Options + Quit.
            int rowY = scaledHeight / 4 + 48 + 72 + 12;
            int x = scaledWidth / 2 - 100 - BLibTitleScreenIntegration.BUTTON_WIDTH - 4;
            Screens.getButtons(screen).add(BLibTitleScreenIntegration.buildButton(x, rowY));
        });
    }

    private BLibFabricTitleScreenIntegration() {
        throw new UnsupportedOperationException();
    }
}

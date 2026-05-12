package com.blib.engine.neoforge;

import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.BLibTitleScreenIntegration;

/**
 * NeoForge wiring for the "Open BLib" button on the main menu. Listens for {@link ScreenEvent.Init.Post} and adds the
 * button to the TitleScreen after vanilla finishes laying out its widgets.
 */
@ApiStatus.Internal
public final class BLibNeoForgeTitleScreenIntegration {

    public static void register() {
        NeoForge.EVENT_BUS.<ScreenEvent.Init.Post>addListener(event -> {
            if (!(event.getScreen() instanceof TitleScreen)) {
                return;
            }
            // Suppress when the engine is wrapping this TitleScreen as its menu-overlay backdrop — the engine init's
            // the wrapped screen, which re-fires Init.Post, and we don't want a duplicate "Open BLib" button to
            // appear inside the viewport rect.
            if (net.minecraft.client.Minecraft.getInstance().screen instanceof com.blib.engine.ui.EngineWorkspaceScreen) {
                return;
            }
            int scaledWidth = event.getScreen().width;
            int scaledHeight = event.getScreen().height;
            int rowY = scaledHeight / 4 + 48 + 72 + 12;
            int x = scaledWidth / 2 - 100 - BLibTitleScreenIntegration.BUTTON_WIDTH - 4;
            event.addListener(BLibTitleScreenIntegration.buildButton(x, rowY));
        });
    }

    private BLibNeoForgeTitleScreenIntegration() {
        throw new UnsupportedOperationException();
    }
}

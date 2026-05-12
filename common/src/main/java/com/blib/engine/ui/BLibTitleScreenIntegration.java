package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * Shared "open BLib from the main menu" entrypoint + button factory. Both loaders' TitleScreen-init hooks call into
 * this class so the open behavior and button label live in one place.
 */
@ApiStatus.Internal
public final class BLibTitleScreenIntegration {

    /**
     * Position the button in TitleScreen's bottom row, to the left of Options. Vanilla lays the row out at
     * {@code height/4 + 48 + 72 + 12}: Realms button is at {@code height/4 + 48 + 72}, then the small row sits 12 px
     * below. Mirror that y; use a slim width so the row stays balanced.
     */
    public static final int BUTTON_WIDTH = 98;

    public static final int BUTTON_HEIGHT = 20;

    private BLibTitleScreenIntegration() {}

    /**
     * Open the engine in {@link EngineWorkspaceScreen.Mode#MENU_OVERLAY} mode, wrapping whatever screen is currently
     * active (typically the {@link net.minecraft.client.gui.screens.TitleScreen} the user just clicked the button on).
     */
    public static void openFromTitleScreen() {
        var mc = Minecraft.getInstance();
        Screen current = mc.screen;
        mc.setScreen(new EngineWorkspaceScreen(current));
    }

    /** Build the "Open BLib" button for the TitleScreen row. Caller positions it by passing x / y. */
    public static Button buildButton(int x, int y) {
        return Button.builder(Component.literal("Open BLib"), btn -> openFromTitleScreen())
            .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build();
    }
}

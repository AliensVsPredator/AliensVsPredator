package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibScreenInitContext;
import com.blib.mod.BLib;

/**
 * Adds the "Open BLib" button to the vanilla {@link TitleScreen} as a single loader-agnostic registration on
 * {@code BLib.MOD.events().postScreenInit()}. Both Fabric and NeoForge ride the same listener — the BLib event handle
 * does the loader-specific bridging in its bridge classes ({@code BLibFabricScreenInitEvents} /
 * {@code BLibNeoForgeScreenInitEvents}).
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

    /** Called once during BLib mod init — registers the cross-loader listener that injects the button. */
    public static void register() {
        BLib.MOD.events().postScreenInit().register(BLibTitleScreenIntegration::onScreenInit);
    }

    private static void onScreenInit(BLibScreenInitContext ctx) {
        if (!(ctx.screen() instanceof TitleScreen titleScreen)) {
            return;
        }
        // Suppress when the engine is wrapping this TitleScreen as its menu-overlay backdrop — the engine init's
        // the wrapped screen, which re-fires the screen-init event, and we don't want a duplicate "Open BLib"
        // button inside the viewport rect (the user would be trying to "open" while the engine is already open).
        if (Minecraft.getInstance().screen instanceof EngineWorkspaceScreen) {
            return;
        }
        int rowY = titleScreen.height / 4 + 48 + 72 + 12;
        int x = titleScreen.width / 2 - 100 - BUTTON_WIDTH - 4;
        ctx.addWidget(buildButton(x, rowY));
    }

    /**
     * Open the engine in {@link EngineWorkspaceScreen.Mode#MENU_OVERLAY} mode, wrapping whatever screen is currently
     * active (typically the {@link TitleScreen} the user just clicked the button on).
     */
    public static void openFromTitleScreen() {
        var mc = Minecraft.getInstance();
        Screen current = mc.screen;
        mc.setScreen(new EngineWorkspaceScreen(current));
    }

    /** Build the "Open BLib" button. Caller positions it by passing x / y. */
    public static Button buildButton(int x, int y) {
        return Button.builder(Component.literal("Open BLib"), btn -> openFromTitleScreen())
            .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build();
    }
}

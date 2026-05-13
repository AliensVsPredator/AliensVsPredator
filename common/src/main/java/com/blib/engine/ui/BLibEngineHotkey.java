package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.screen.ProjectPickerScreen;

/**
 * Universal "toggle the BLib engine" entry-point fired by the {@code TOGGLE_ENGINE} keybinding (see
 * {@code MixinKeyboardHandler_EngineHotkey} and {@code BLibKeyBindings}). Picks the right flow based on engine state:
 * <ul>
 * <li>Engine already open: route through {@link EngineWorkspaceScreen#closeEngine()} so the close bypasses the
 * setScreen-redirect mixin. The wrapped screen (if any) is dropped, and the user lands back in whichever vanilla screen
 * / in-game view they were in.</li>
 * <li>Engine closed: open the {@link ProjectPickerScreen}. The picker is the consistent gateway in both contexts —
 * in-world it confirms-to {@link EngineWorkspaceScreen.Mode#IN_GAME}; from a menu screen it confirms-to
 * {@link EngineWorkspaceScreen.Mode#MENU_OVERLAY} wrapping the screen the user pressed the hotkey on. Cancel from the
 * picker returns the user to wherever they came from (vanilla's {@code setScreen(null)} substitutes TitleScreen when no
 * world is loaded, or drops to game when in-world).</li>
 * </ul>
 */
@ApiStatus.Internal
public final class BLibEngineHotkey {

    private BLibEngineHotkey() {}

    public static void toggle() {
        var mc = Minecraft.getInstance();
        if (mc.screen instanceof EngineWorkspaceScreen) {
            EngineWorkspaceScreen.closeEngine();
            return;
        }
        // Treat B-on-picker as "close the picker" — opening another picker would capture the current picker as
        // previousScreen, so the eventual workspace open would wrap it (picker rendered inside the viewport, with
        // an OBS-style recursion when the nested picker is cancelled).
        if (mc.screen instanceof ProjectPickerScreen) {
            mc.setScreen(null);
            return;
        }
        // Skip the picker when a project is already active — the user picks once per game session, then B should
        // toggle the workspace directly. Switching projects mid-session goes through the workspace's File menu.
        if (ProjectSession.activeProject() != null) {
            openWorkspaceFromPicker(mc.screen);
            return;
        }
        // Capture the screen the user was on BEFORE the picker takes over. If the picker confirms in menu-overlay
        // mode (no world), the workspace wraps this screen so the viewport still shows the menu they came from.
        Screen previousScreen = mc.screen;
        mc.setScreen(new ProjectPickerScreen(() -> openWorkspaceFromPicker(previousScreen)));
    }

    /**
     * True when the hotkey should be intercepted vs passed through to vanilla. Returns false (= pass through) when any
     * text-input widget has focus on the active screen or — when the engine is wrapping a menu — on the wrapped screen.
     * Lets the user type the bound character into chat, world-name fields, search boxes, sign / book editors, etc.,
     * including BLib's own {@code TextInput} which is used widely inside the engine workspace.
     */
    public static boolean shouldInterceptHotkey() {
        var mc = Minecraft.getInstance();
        if (com.blib.engine.ui.widget.TextInput.getFocused() != null) {
            return false;
        }
        if (mc.screen != null) {
            var focused = mc.screen.getFocused();
            if (focused instanceof EditBox || focused instanceof MultiLineEditBox) {
                return false;
            }
            if (mc.screen instanceof EngineWorkspaceScreen engine) {
                var wrapped = engine.wrappedScreen();
                if (wrapped != null) {
                    var wrappedFocused = wrapped.getFocused();
                    if (wrappedFocused instanceof EditBox || wrappedFocused instanceof MultiLineEditBox) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void openWorkspaceFromPicker(@Nullable Screen previousScreen) {
        var mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.setScreen(new EngineWorkspaceScreen());
        } else {
            // Menu-overlay confirms-to: wrap whatever screen the user was on before the picker so the viewport shows
            // the same menu, plus their chosen project is active for project-aware panels.
            mc.setScreen(new EngineWorkspaceScreen(previousScreen));
        }
    }
}

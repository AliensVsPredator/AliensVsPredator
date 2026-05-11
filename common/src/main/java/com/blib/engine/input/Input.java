package com.blib.engine.input;

import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Trigger for a {@link Keybinding} — a keyboard key, mouse button, drag, scroll, or modifier-alone. {@link #format}
 * returns the canonical human-readable display string used by the status bar and any future cheat-sheet / toolbar
 * surfaces, e.g. {@code "Ctrl+Z"}, {@code "LMB"}, {@code "Shift+MMB-drag"}, {@code "Scroll"}, {@code "Alt"}.
 */
@ApiStatus.Internal
public sealed interface Input {

    int MOD_CTRL = GLFW.GLFW_MOD_CONTROL;

    int MOD_SHIFT = GLFW.GLFW_MOD_SHIFT;

    int MOD_ALT = GLFW.GLFW_MOD_ALT;

    /** Bits considered for display + handler matching. NUM_LOCK / CAPS_LOCK / SUPER are intentionally ignored. */
    int DISPLAY_MOD_MASK = MOD_CTRL | MOD_SHIFT | MOD_ALT;

    String format();

    record Key(
        int keyCode,
        int modifierMask
    ) implements Input {

        @Override
        public String format() {
            return modPrefix(modifierMask) + keyName(keyCode);
        }
    }

    record MouseButton(
        int button,
        int modifierMask
    ) implements Input {

        @Override
        public String format() {
            return modPrefix(modifierMask) + buttonName(button);
        }
    }

    record MouseDrag(
        int button,
        int modifierMask
    ) implements Input {

        @Override
        public String format() {
            return modPrefix(modifierMask) + buttonName(button) + "-drag";
        }
    }

    record Scroll(int modifierMask) implements Input {

        @Override
        public String format() {
            return modPrefix(modifierMask) + "Scroll";
        }
    }

    /** Holding a modifier alone (no companion key/button) — e.g. holding Alt while dragging to bypass grid snap. */
    record Modifier(int modifierMask) implements Input {

        @Override
        public String format() {
            var s = modPrefix(modifierMask);
            return s.isEmpty() ? "" : s.substring(0, s.length() - 1);
        }
    }

    private static String modPrefix(int mask) {
        var sb = new StringBuilder();
        if ((mask & MOD_CTRL) != 0) {
            sb.append("Ctrl+");
        }
        if ((mask & MOD_SHIFT) != 0) {
            sb.append("Shift+");
        }
        if ((mask & MOD_ALT) != 0) {
            sb.append("Alt+");
        }
        return sb.toString();
    }

    /**
     * Localized display name for a keyboard key (via MC's {@link InputConstants}). Returns e.g. "R", "Esc", "Delete".
     */
    private static String keyName(int keyCode) {
        return InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString();
    }

    /** Short label for a mouse button — friendlier than MC's default "Button N". */
    private static String buttonName(int button) {
        return switch (button) {
            case 0 -> "LMB";
            case 1 -> "RMB";
            case 2 -> "MMB";
            default -> "Mouse " + button;
        };
    }
}

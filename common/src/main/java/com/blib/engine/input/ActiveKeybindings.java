package com.blib.engine.input;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Resolver layer between handlers and {@link Keybindings}. Handlers call {@code matchesKey/MouseButton/Scroll} here
 * instead of comparing literal {@code GLFW_KEY_*} constants — so the active {@link KeybindingProfile}'s overrides take
 * effect at the point of match.
 * <p>
 * The active profile is held in a single volatile reference and swapped atomically by {@link KeybindingProfileCatalog}.
 * Reads are lock-free; concurrent writes are not expected (UI thread only).
 */
@ApiStatus.Internal
public final class ActiveKeybindings {

    private static volatile KeybindingProfile active = KeybindingProfile.defaultProfile();

    private ActiveKeybindings() {}

    static void setActive(KeybindingProfile profile) {
        active = profile;
    }

    public static KeybindingProfile getActive() {
        return active;
    }

    /**
     * The currently-resolved binding for {@code defaultBinding} — override applied if present, otherwise the default.
     */
    public static Keybinding resolve(Keybinding defaultBinding) {
        var override = active.overrides().get(defaultBinding.id());
        return override == null ? defaultBinding : new Keybinding(defaultBinding.id(), defaultBinding.label(), override);
    }

    public static boolean matchesKey(Keybinding defaultBinding, int keyCode, int modifiers) {
        return resolve(defaultBinding).matchesKey(keyCode, modifiers);
    }

    public static boolean matchesMouseButton(Keybinding defaultBinding, int button, int modifiers) {
        return resolve(defaultBinding).matchesMouseButton(button, modifiers);
    }

    /** {@link #matchesMouseButton(Keybinding, int, int)} using {@link #currentMods}. */
    public static boolean matchesMouseButton(Keybinding defaultBinding, int button) {
        return matchesMouseButton(defaultBinding, button, currentMods());
    }

    public static boolean matchesScroll(Keybinding defaultBinding, int modifiers) {
        return resolve(defaultBinding).matchesScroll(modifiers);
    }

    public static boolean matchesScroll(Keybinding defaultBinding) {
        return matchesScroll(defaultBinding, currentMods());
    }

    /**
     * For {@link Input.Modifier} bindings: true iff every modifier bit in the binding's mask is currently held. For
     * non-modifier inputs (Key / MouseButton / MouseDrag / Scroll), always returns false — use the appropriate
     * {@code matches*} method instead.
     */
    public static boolean isModifierHeld(Keybinding defaultBinding) {
        var resolved = resolve(defaultBinding).input();
        if (!(resolved instanceof Input.Modifier mod)) {
            return false;
        }
        var mask = mod.modifierMask() & Input.DISPLAY_MOD_MASK;
        return mask != 0 && (currentMods() & mask) == mask;
    }

    /**
     * Snapshot the current modifier-key state. For callsites that don't receive {@code modifiers} as a parameter (e.g.
     * {@code mouseClicked(double, double, int)}), this reconstructs the GLFW mod mask from
     * {@link Screen#hasControlDown()}/{@link Screen#hasShiftDown()}/{@link Screen#hasAltDown()}.
     */
    public static int currentMods() {
        var m = 0;
        if (Screen.hasControlDown()) {
            m |= GLFW.GLFW_MOD_CONTROL;
        }
        if (Screen.hasShiftDown()) {
            m |= GLFW.GLFW_MOD_SHIFT;
        }
        if (Screen.hasAltDown()) {
            m |= GLFW.GLFW_MOD_ALT;
        }
        return m;
    }
}

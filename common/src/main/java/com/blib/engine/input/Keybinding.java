package com.blib.engine.input;

import org.jetbrains.annotations.ApiStatus;

/**
 * A named input binding — the canonical (id, label, input) tuple consumed by display surfaces (the status bar's hint
 * region, future cheat-sheet overlay, future toolbar hotkey labels) so that renaming or rebinding any binding stays
 * single-source.
 * <p>
 * Bindings are declared as static constants on {@link Keybindings}. The {@code matches*} predicates are designed for
 * future handler migration (so {@code keyPressed} sites can drop their literal {@code GLFW_KEY_*} checks) but are not
 * yet consumed — v1 routes display through this class only.
 * <p>
 * {@link #withLabel} returns a copy with a substituted label, used by callers that want to inject live state into the
 * displayed hint (e.g. {@code "Cycle mode (FREE)"}) without mutating the canonical instance.
 */
@ApiStatus.Internal
public record Keybinding(
    String id,
    String label,
    Input input
) {

    public String formatInput() {
        return input.format();
    }

    public boolean matchesKey(int keyCode, int modifiers) {
        return input instanceof Input.Key k
            && k.keyCode() == keyCode
            && (modifiers & Input.DISPLAY_MOD_MASK) == (k.modifierMask() & Input.DISPLAY_MOD_MASK);
    }

    public boolean matchesMouseButton(int button, int modifiers) {
        var displayMods = modifiers & Input.DISPLAY_MOD_MASK;
        return (input instanceof Input.MouseButton b
            && b.button() == button
            && displayMods == (b.modifierMask() & Input.DISPLAY_MOD_MASK))
            || (input instanceof Input.MouseDrag d
                && d.button() == button
                && displayMods == (d.modifierMask() & Input.DISPLAY_MOD_MASK));
    }

    public boolean matchesScroll(int modifiers) {
        return input instanceof Input.Scroll s
            && (modifiers & Input.DISPLAY_MOD_MASK) == (s.modifierMask() & Input.DISPLAY_MOD_MASK);
    }

    public Keybinding withLabel(String newLabel) {
        return new Keybinding(id, newLabel, input);
    }
}

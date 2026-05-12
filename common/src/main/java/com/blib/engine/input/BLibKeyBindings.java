package com.blib.engine.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Vanilla-system {@link KeyMapping}s owned by the BLib engine. Defined once in common; each loader registers them with
 * its keybinding system (Fabric's {@code KeyBindingHelper}, NeoForge's {@code RegisterKeyMappingsEvent}) so they appear
 * in MC's Controls / Keybindings menu and can be rebound by the player. The {@code MixinKeyboardHandler_EngineHotkey}
 * reads {@link #TOGGLE_ENGINE}'s currently-bound key each press so a rebind takes effect immediately.
 */
@ApiStatus.Internal
public final class BLibKeyBindings {

    public static final String CATEGORY = "key.categories.blib";

    /** Toggle the BLib engine workspace. Default: B. Works in-game and on menu screens. */
    public static final KeyMapping TOGGLE_ENGINE = new KeyMapping(
        "key.blib.toggle_engine",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        CATEGORY
    );

    private BLibKeyBindings() {}
}

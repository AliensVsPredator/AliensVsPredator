package com.blib.engine.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.input.BLibKeyBindings;

/**
 * Fabric-side registration for BLib's vanilla {@link net.minecraft.client.KeyMapping}s. {@code KeyBindingHelper} adds
 * them to MC's controls menu so the player can rebind. Called once during client init.
 */
@ApiStatus.Internal
public final class BLibFabricKeyBindings {

    public static void register() {
        KeyBindingHelper.registerKeyBinding(BLibKeyBindings.TOGGLE_ENGINE);
    }

    private BLibFabricKeyBindings() {}
}

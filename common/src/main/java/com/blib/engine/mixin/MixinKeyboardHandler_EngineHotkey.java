package com.blib.engine.mixin;

import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.engine.input.BLibKeyBindings;
import com.blib.engine.ui.BLibEngineHotkey;

/**
 * Universal toggle hotkey for the BLib engine workspace. Hooks at the {@link KeyboardHandler} level (before MC's
 * per-screen dispatch and before the in-game KeyMapping pass) so it works whether the player is in-game, on the title
 * screen, in a world-select menu, etc.
 * <p>
 * The bound key is whichever {@link BLibKeyBindings#TOGGLE_ENGINE} resolves to at press time — by default B, but the
 * player can rebind through MC's Controls menu and the change takes effect on the next press.
 * <p>
 * Pass-through gates: action != PRESS (repeat / release skipped); the keypress doesn't match the binding; or a text
 * input has focus (delegated to {@link BLibEngineHotkey#shouldInterceptHotkey()}). When the hotkey fires,
 * {@link BLibEngineHotkey#toggle()} decides between open and close based on whether the engine is the active screen.
 */
@Mixin(KeyboardHandler.class)
public abstract class MixinKeyboardHandler_EngineHotkey {

    @Inject(method = "keyPress(JIIII)V", at = @At("HEAD"), cancellable = true)
    private void blib$toggleEngineHotkey(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }
        if (!BLibKeyBindings.TOGGLE_ENGINE.matches(key, scanCode)) {
            return;
        }
        if (!BLibEngineHotkey.shouldInterceptHotkey()) {
            return;
        }
        BLibEngineHotkey.toggle();
        ci.cancel();
    }
}

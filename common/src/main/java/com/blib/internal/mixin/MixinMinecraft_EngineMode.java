package com.blib.internal.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.engine.EngineMode;

/**
 * Suppress player keybinds while engine mode is active. Cancels {@code Minecraft.handleKeybinds} entirely (so attack,
 * use, inventory open, drop, hotbar select, perspective toggle, etc. don't fire), but manually handles chat and
 * command-key opening so the user can still type {@code /blib engine} to exit and run other commands.
 * <p>
 * Pending clicks for the noisy keys are drained too — without this, every click queued during engine mode would fire in
 * a burst as soon as the user exited (multiple inventory pops, swings, etc.).
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft_EngineMode {

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void blib$skipPlayerKeybindsInEngineMode(CallbackInfo ci) {
        if (!EngineMode.get().isActive()) {
            return;
        }

        var self = (Minecraft) (Object) this;
        var options = self.options;

        // Allow chat / command opening — the user needs at minimum to type `/blib engine` to exit.
        while (options.keyChat.consumeClick()) {
            self.setScreen(new ChatScreen(""));
        }

        if (self.screen == null && options.keyCommand.consumeClick()) {
            self.setScreen(new ChatScreen("/"));
        }

        // Drain noisy click-driven keys so they don't queue up and fire in a burst on exit.
        drain(options.keyAttack);
        drain(options.keyUse);
        drain(options.keyPickItem);
        drain(options.keyInventory);
        drain(options.keyDrop);
        drain(options.keySwapOffhand);
        drain(options.keyAdvancements);
        drain(options.keySocialInteractions);
        drain(options.keyTogglePerspective);
        drain(options.keySmoothCamera);

        for (var slot : options.keyHotbarSlots) {
            drain(slot);
        }

        ci.cancel();
    }

    private static void drain(net.minecraft.client.KeyMapping mapping) {
        while (mapping.consumeClick()) {
            // discard
        }
    }
}

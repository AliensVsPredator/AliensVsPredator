package com.blib.engine.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.engine.ui.EngineWorkspaceScreen;

/**
 * While the engine workspace is open in menu-overlay mode, intercept {@link Minecraft#setScreen(Screen)} and redirect
 * the swap to the engine's wrapped-screen slot instead of replacing the engine itself. Lets the user navigate
 * TitleScreen → SelectWorldScreen → LevelLoadingScreen → in-world while the engine stays on top the whole time.
 * <p>
 * The redirect stands down when: the engine is in {@link EngineWorkspaceScreen.Mode#IN_GAME} mode (this mixin is a
 * no-op so vanilla deathscreen substitution and normal screen flow proceed); the explicit close path is in flight
 * ({@code preparingToClose} flag); the new screen IS the engine itself (initial open from TitleScreen — we want that to
 * happen, not loop into ourselves).
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft_EngineScreenRedirect {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void blib$redirectToEngineWrap(@Nullable Screen guiScreen, CallbackInfo ci) {
        var mc = (Minecraft) (Object) this;
        if (!(mc.screen instanceof EngineWorkspaceScreen engine)) {
            return;
        }
        if (!engine.isWrappingMenus()) {
            return;
        }
        if (EngineWorkspaceScreen.isPreparingToClose()) {
            return;
        }
        if (guiScreen == engine) {
            return;
        }
        engine.setWrappedScreen(guiScreen);
        ci.cancel();
    }
}

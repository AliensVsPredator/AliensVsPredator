package com.blib.engine.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.engine.ui.EngineWorkspaceScreen;

/**
 * Skip {@link Screen#renderBlurredBackground} while the engine is rendering a wrapped menu screen into its offscreen
 * RT. Vanilla's method ends with {@code Minecraft.getInstance().getMainRenderTarget().bindWrite(false)}, which rebinds
 * the main framebuffer mid-render and silently invalidates our offscreen RT — every widget / text / menu-background
 * draw after that point goes to the main RT instead of the offscreen one, leaving the viewport showing only the
 * panorama (which renders before the blur call) and leaking the rest of the wrapped screen onto the engine's panels.
 * <p>
 * Cancelling is safe in this context because the blur targets the main RT, which we don't composit into the viewport
 * anyway — the panorama is the menu-mode visual that the user sees. Outside the engine's wrapped-render pass the cancel
 * is gated off, so vanilla blur behavior is unchanged when the engine isn't active.
 */
@Mixin(Screen.class)
public abstract class MixinScreen_EngineSkipBlur {

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
    private void blib$skipBlurInWrappedRender(float partialTick, CallbackInfo ci) {
        if (EngineWorkspaceScreen.isInWrappedScreenRender()) {
            ci.cancel();
        }
    }
}

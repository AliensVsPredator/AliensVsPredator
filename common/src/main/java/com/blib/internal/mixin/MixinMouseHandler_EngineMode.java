package com.blib.internal.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.engine.EngineMode;

/**
 * Routes raw mouse input to the engine session while engine mode is active.
 * <ul>
 * <li>{@code turnPlayer}: forwards accumulated mouse delta to the session and cancels the player's view rotation. Only
 * runs when the mouse is grabbed (vanilla gates the call) — used by fly mode.</li>
 * <li>{@code handleAccumulatedMovement}: captures delta when the mouse is <em>not</em> grabbed (orbit mode releases the
 * mouse so the cursor is visible). We pull the delta out of the accumulators before vanilla's per-frame zeroing.</li>
 * <li>{@code onScroll}: forwards the wheel delta to the session and cancels (so vanilla doesn't swap hotbar slots or
 * adjust creative-flight speed). Skipped when a screen is open so chat scroll-back still works.</li>
 * </ul>
 */
@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler_EngineMode {

    @Accessor("accumulatedDX")
    public abstract double blib$getAccumulatedDX();

    @Accessor("accumulatedDX")
    public abstract void blib$setAccumulatedDX(double v);

    @Accessor("accumulatedDY")
    public abstract double blib$getAccumulatedDY();

    @Accessor("accumulatedDY")
    public abstract void blib$setAccumulatedDY(double v);

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void blib$captureTurnForEngineMode(double movementTime, CallbackInfo ci) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        session.addMouseDelta(blib$getAccumulatedDX(), blib$getAccumulatedDY());
        blib$setAccumulatedDX(0);
        blib$setAccumulatedDY(0);
        ci.cancel();
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"))
    private void blib$captureUngrabbedDeltaForEngineMode(CallbackInfo ci) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        var self = (MouseHandler) (Object) this;

        // When grabbed, turnPlayer will run later in this method and our injection there handles capture. Only run
        // here for the ungrabbed case (orbit mode), otherwise we'd double-capture.
        if (self.isMouseGrabbed()) {
            return;
        }

        session.addMouseDelta(blib$getAccumulatedDX(), blib$getAccumulatedDY());
        blib$setAccumulatedDX(0);
        blib$setAccumulatedDY(0);
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void blib$captureScrollForEngineMode(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        // If a screen is open (e.g. chat history), let it handle the scroll.
        if (Minecraft.getInstance().screen != null) {
            return;
        }

        session.addScroll(yOffset);
        ci.cancel();
    }

    /**
     * Suppresses vanilla's "click into world re-grabs the mouse" behaviour while engine mode is active. In orbit mode
     * the mouse is intentionally released so the user has a visible cursor; the auto-grab on click would jump the
     * cursor to screen center, mid-gesture, and break click-to-select (the cursor coordinates we read for the selection
     * raycast end up centered instead of at the click point).
     */
    @Redirect(
        method = "onPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;grabMouse()V")
    )
    private void blib$skipAutoGrabInEngineMode(MouseHandler self) {
        if (EngineMode.get().isActive()) {
            return;
        }

        self.grabMouse();
    }

    /**
     * Buffers LMB press / release into the engine session the moment {@code onPress} fires. Polling
     * {@code isLeftPressed} from the tick handler would miss fast clicks (press + release within the same tick window),
     * so we capture these as discrete events instead.
     */
    @Inject(method = "onPress", at = @At("HEAD"))
    private void blib$captureLmbEventsForEngineMode(long windowPointer, int button, int action, int mods, CallbackInfo ci) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        // Only LMB matters for selection; RMB pan is held-state, no edge detection needed.
        if (button != 0) {
            return;
        }

        if (action == 1) {
            session.notifyLmbPressed();
        } else if (action == 0) {
            session.notifyLmbReleased();
        }
    }
}

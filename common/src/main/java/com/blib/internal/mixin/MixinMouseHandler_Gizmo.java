package com.blib.internal.mixin;

import com.blib.api.client.render.v1.item.BLibGizmoInput;
import com.blib.api.client.render.v1.item.BLibGizmoMode;
import com.blib.api.client.render.v1.item.BLibGizmoState;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Routes raw mouse input to the transform-tuner gizmo while chat is open and gizmo mode is enabled. Gizmo
 * interaction is gated on the chat screen specifically because that's the only screen state where the held
 * item is still rendered in 3D — inventory and pause screens swap the view out, so a gizmo drag would have
 * nothing to act against.
 * <ul>
 *   <li>{@code onPress}: LMB press → try to start a drag, cancelling the event if a handle was hit so the
 *       chat screen doesn't see the click. LMB release → end any active drag.</li>
 *   <li>{@code onMove}: when a drag is active, forward the new cursor position to the gizmo so it can
 *       update the override transform in real time.</li>
 * </ul>
 */
@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler_Gizmo {

    private static final Logger BLIB$LOGGER = LogUtils.getLogger();

    /**
     * One-time mixin-applied signal — fires the first time MouseHandler.setup runs (game startup, called
     * once per JVM). If you don't see this line in logs/latest.log, the mixin isn't being applied at all,
     * regardless of whether the gizmo command appears to work. (The gizmo command lives in a non-mixin
     * class so it can succeed even when mixins are entirely broken.)
     */
    @Inject(method = "setup", at = @At("RETURN"))
    private void blib$logMixinAlive(long windowPointer, CallbackInfo ci) {
        BLIB$LOGGER.info("[BLibGizmo] mixin applied: MouseHandler.setup observed");
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void blib$captureLmbForGizmo(long windowPointer, int button, int action, int mods, CallbackInfo ci) {
        // Only LMB. RMB / scroll wheel are unused by the gizmo and shouldn't be intercepted.
        if (button != 0) {
            return;
        }

        // Logfile output when gizmo is on — unobtrusive, helps diagnostics if something breaks. Chat
        // output goes only behind the trace toggle (`/blib transform-tune debug trace`) since chat
        // messages physically block the user from interacting with the gizmo.
        if (BLibGizmoState.mode() != BLibGizmoMode.OFF) {
            BLIB$LOGGER.info("[BLibGizmo] mixin: onPress button=0 action={} mode={} chatOpen={}",
                action, BLibGizmoState.mode(), isChatOpen());

            if (BLibGizmoInput.isTraceEnabled()) {
                var mc = Minecraft.getInstance();

                if (mc.player != null) {
                    mc.player.sendSystemMessage(Component.literal(
                        "[gizmo] LMB action=" + action + " mode=" + BLibGizmoState.mode() + " chatOpen=" + isChatOpen()
                    ));
                }
            }
        }

        if (BLibGizmoState.mode() == BLibGizmoMode.OFF) {
            return;
        }

        if (!isChatOpen()) {
            return;
        }

        var mouseHandler = Minecraft.getInstance().mouseHandler;

        if (action == 1) {
            // GLFW press. Read the current cursor position — onPress doesn't get it as an arg.
            var started = BLibGizmoInput.tryStartDrag(mouseHandler.xpos(), mouseHandler.ypos());

            if (started) {
                // Cancel so the chat screen doesn't process the click (which would deselect the chat input
                // or perform other unwanted screen actions).
                ci.cancel();
            }
        } else if (action == 0) {
            // GLFW release. End any drag, regardless of whether the press was for us — we always want a
            // release to clear drag state.
            if (BLibGizmoState.isDragging()) {
                BLibGizmoInput.endDrag();
                ci.cancel();
            }
        }
    }

    @Inject(method = "onMove", at = @At("HEAD"))
    private void blib$captureMoveForGizmo(long windowPointer, double xpos, double ypos, CallbackInfo ci) {
        if (!BLibGizmoState.isDragging()) {
            return;
        }

        // If chat closed mid-drag, end the drag so it doesn't keep updating against a now-locked cursor.
        // This also handles the user pressing Escape — the drag terminates cleanly with whatever value the
        // last-pre-close frame produced.
        if (!isChatOpen()) {
            BLibGizmoInput.endDrag();
            return;
        }

        // Note: we don't cancel the event — chat / Screen still benefit from cursor tracking for hover
        // effects and selection. We just sample the position to drive the drag.
        BLibGizmoInput.updateDrag(xpos, ypos);
    }

    private static boolean isChatOpen() {
        return Minecraft.getInstance().screen instanceof ChatScreen;
    }
}

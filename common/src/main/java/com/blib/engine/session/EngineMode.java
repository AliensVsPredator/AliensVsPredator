package com.blib.engine.session;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.BLibAPI;

/**
 * Dev-only in-game editor mode. While active, the player camera is detached, player input is suppressed, the vanilla
 * HUD and first-person hand are hidden, and the screen is taken over by an editor HUD that lets you click-select
 * objects and manipulate them via 3D gizmos.
 * <p>
 * Toggled by {@code /blib engine}. Gated behind {@link BLibAPI#isDevelopmentEnvironment()} at the command-registration
 * site, so this class is never reachable from a shipped build.
 */
@ApiStatus.Internal
public final class EngineMode {

    private static final EngineMode INSTANCE = new EngineMode();

    /**
     * Volatile because the toggle command runs on the integrated-server thread while every reader (mixins, freecam
     * tick, HUD render) runs on the client thread. No races worth handling beyond visibility.
     */
    private volatile @Nullable EngineSession session;

    /** Saved {@code Options.hideGui} value at engine entry, restored on exit. {@code hideGui} is session-only state. */
    private boolean prevHideGui;

    private EngineMode() {}

    public static EngineMode get() {
        return INSTANCE;
    }

    public boolean isActive() {
        return session != null;
    }

    public @Nullable EngineSession session() {
        return session;
    }

    /**
     * Enter engine mode. Captures the player's current eye position/rotation as the freecam starting transform so the
     * camera doesn't snap when control flips over, and toggles {@code Options.hideGui} so the vanilla HUD disappears.
     */
    public void enter() {
        if (session != null) {
            return;
        }

        var mc = Minecraft.getInstance();
        var player = mc.player;

        if (player == null) {
            return;
        }

        prevHideGui = mc.options.hideGui;
        mc.options.hideGui = true;

        var eye = player.getEyePosition(1.0F);
        session = new EngineSession(eye.x, eye.y, eye.z, player.getYRot(), player.getXRot());

        // Request the server's PlacedPiece set for the current dimension so the client's hover / selection mirror is
        // populated for the very first frame of engine mode. The reply broadcasts to all engine-mode players, but in
        // practice this is singleplayer + dev so we don't care about ordering with other clients.
        com.blib.mod.BLib.MOD.networking()
            .sendToServer(
                new com.blib.mod.common.network.packet.C2SRequestPlacedPiecesPayload(player.level().dimension().location())
            );
    }

    /**
     * Leave engine mode. Force-restores all hijacked state regardless of session details — the next frame should look
     * indistinguishable from a never-entered state. If we exited from orbit mode (mouse released), re-grab so the
     * player isn't dropped back into gameplay with a free cursor.
     */
    public void exit() {
        if (session == null) {
            return;
        }

        var mc = Minecraft.getInstance();
        mc.options.hideGui = prevHideGui;

        if (mc.screen == null && !mc.mouseHandler.isMouseGrabbed()) {
            mc.mouseHandler.grabMouse();
        }

        // Reset the active selection tool so a Marquee-mode session doesn't bleed into the next engine entry; the
        // workspace's other transient selections (held jigsaw piece, armed entity type) already reset on Esc cascade
        // or via the screen's close lifecycle, but the selection tool persists statically and needs an explicit clear.
        SelectionToolState.reset();

        // Pending tag-edits from the inspector are also static; clear so an uncommitted overlay doesn't shadow the
        // runtime registry the next time the user enters the engine.
        com.blib.engine.tag.TagStagingCache.clear();

        // Drop the placed-piece mirror so a stale set from this dimension doesn't ghost into the next engine entry
        // (e.g. after the player travels and re-enters elsewhere). On next entry we re-request.
        com.blib.engine.jigsaw.ClientPlacedPieceRegistry.clear();

        session = null;
    }

    public void toggle() {
        if (isActive()) {
            exit();
        } else {
            enter();
        }
    }
}

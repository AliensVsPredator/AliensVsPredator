package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Pause / resume the integrated server's tick rate from the workspace UI. Equivalent to {@code /tick freeze} and
 * {@code /tick unfreeze}, but invokable in-process so the play / pause button can flip state without going through the
 * command dispatcher.
 * <p>
 * On {@link #captureAndPause()} the current freeze state is saved so {@link #restore()} can put it back on workspace
 * close — that way opening + closing the workspace doesn't unfreeze a server that was already frozen for unrelated
 * reasons (e.g. the user manually ran {@code /tick freeze} before opening the editor).
 * <p>
 * Multiplayer clients have no integrated server, so all calls no-op cleanly there. A future pass could wire to a
 * networked freeze packet for multiplayer; for the editor's dev-loop use case singleplayer is the realistic target.
 */
@ApiStatus.Internal
public final class EngineTickControl {

    private static @Nullable Boolean savedFrozenState;

    private EngineTickControl() {}

    public static boolean isPaused() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return false;
        }

        return server.tickRateManager().isFrozen();
    }

    public static void setPaused(boolean paused) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }

        // setFrozen mutates server state — schedule on the server thread.
        server.execute(() -> server.tickRateManager().setFrozen(paused));
    }

    public static void toggle() {
        setPaused(!isPaused());
    }

    /**
     * Advance the integrated server by {@code ticks} game ticks while paused — equivalent to {@code /tick step <n>t}.
     * No-op outside singleplayer or when the server isn't currently frozen (the underlying API only steps a frozen
     * game). Used by the toolbar's step button so the user can scrub forward one tick at a time without leaving the
     * editor.
     */
    public static void step(int ticks) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }
        server.execute(() -> server.tickRateManager().stepGameIfPaused(ticks));
    }

    /**
     * Save the current freeze state and force-pause. Called from the workspace screen constructor so the world is
     * frozen the moment the editor opens.
     */
    public static void captureAndPause() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }

        savedFrozenState = server.tickRateManager().isFrozen();
        server.execute(() -> server.tickRateManager().setFrozen(true));
    }

    /**
     * Restore whatever freeze state was active before the workspace opened. No-op when {@link #captureAndPause()}
     * wasn't called first (e.g. multiplayer session).
     */
    public static void restore() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        var state = savedFrozenState;
        savedFrozenState = null;

        if (server == null || state == null) {
            return;
        }

        var target = state;
        server.execute(() -> server.tickRateManager().setFrozen(target));
    }
}

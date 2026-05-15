package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Pause / resume the integrated server's tick rate from the workspace UI. Equivalent to {@code /tick freeze},
 * {@code /tick unfreeze}, {@code /tick step}, and {@code /tick rate}, but invokable in-process so the viewport
 * transport buttons can flip state without going through the command dispatcher.
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

    private static final float DEFAULT_TICK_RATE = 20.0F;

    private static final int[] STEP_TICK_OPTIONS = { 1, 5, 20, 100 };

    private static final int[] FAST_FORWARD_MULTIPLIER_OPTIONS = { 2, 5, 10, 20 };

    private static @Nullable Boolean savedFrozenState;

    private static @Nullable Float savedTickRate;

    private static int stepTickOption;

    private static int fastForwardMultiplierOption;

    private static boolean fastForwarding;

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

        fastForwarding = false;
        var tickRate = normalTickRate();
        // setFrozen mutates server state — schedule on the server thread.
        server.execute(() -> {
            server.tickRateManager().setTickRate(tickRate);
            server.tickRateManager().setFrozen(paused);
        });
    }

    public static void toggle() {
        setPaused(!isPaused());
    }

    /**
     * Advance the integrated server by the selected number of game ticks while paused — equivalent to
     * {@code /tick step <n>t}. No-op outside singleplayer or when the server isn't currently frozen (the underlying
     * API only steps a frozen game). Used by the toolbar's step button so the user can scrub forward without leaving
     * the editor.
     */
    public static void step() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }
        var ticks = selectedStepTicks();
        server.execute(() -> server.tickRateManager().stepGameIfPaused(ticks));
    }

    public static int selectedStepTicks() {
        return STEP_TICK_OPTIONS[stepTickOption];
    }

    public static void cycleStepTicks() {
        stepTickOption = (stepTickOption + 1) % STEP_TICK_OPTIONS.length;
    }

    public static boolean isFastForwarding() {
        return fastForwarding;
    }

    public static int selectedFastForwardMultiplier() {
        return FAST_FORWARD_MULTIPLIER_OPTIONS[fastForwardMultiplierOption];
    }

    public static void cycleFastForwardMultiplier() {
        fastForwardMultiplierOption = (fastForwardMultiplierOption + 1) % FAST_FORWARD_MULTIPLIER_OPTIONS.length;
        if (fastForwarding) {
            setFastForwarding(true);
        }
    }

    public static void toggleFastForward() {
        setFastForwarding(!fastForwarding);
    }

    private static void setFastForwarding(boolean enabled) {
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }

        fastForwarding = enabled;
        var tickRate = enabled ? normalTickRate() * selectedFastForwardMultiplier() : normalTickRate();
        server.execute(() -> {
            server.tickRateManager().setTickRate(tickRate);
            server.tickRateManager().setFrozen(false);
        });
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
        savedTickRate = server.tickRateManager().tickrate();
        fastForwarding = false;
        var tickRate = normalTickRate();
        server.execute(() -> {
            server.tickRateManager().setTickRate(tickRate);
            server.tickRateManager().setFrozen(true);
        });
    }

    /**
     * Restore whatever freeze state was active before the workspace opened. No-op when {@link #captureAndPause()}
     * wasn't called first (e.g. multiplayer session).
     */
    public static void restore() {
        var server = Minecraft.getInstance().getSingleplayerServer();
        var state = savedFrozenState;
        var tickRate = savedTickRate;
        savedFrozenState = null;
        savedTickRate = null;
        fastForwarding = false;

        if (server == null || state == null) {
            return;
        }

        var target = state;
        var targetTickRate = tickRate != null ? tickRate : DEFAULT_TICK_RATE;
        server.execute(() -> {
            server.tickRateManager().setTickRate(targetTickRate);
            server.tickRateManager().setFrozen(target);
        });
    }

    private static float normalTickRate() {
        return savedTickRate != null ? savedTickRate : DEFAULT_TICK_RATE;
    }
}

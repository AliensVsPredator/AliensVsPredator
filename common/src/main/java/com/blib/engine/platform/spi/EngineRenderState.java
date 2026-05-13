package com.blib.engine.platform.spi;

import org.jetbrains.annotations.ApiStatus;

/**
 * Render-state flags exposed to mixins as a narrow SPI. Mixins import this instead of reaching directly into the
 * workspace screen class so the mixin layer doesn't bake in the screen's identity.
 * <p>
 * Both flags are volatile because they're written from the client thread (the workspace render / close path) and read
 * from mixin call sites that may run on the same thread but at different points in the frame. No races worth handling
 * beyond visibility.
 */
@ApiStatus.Internal
public final class EngineRenderState {

    private static volatile boolean preparingToClose;

    private static volatile boolean inWrappedScreenRender;

    private EngineRenderState() {}

    /**
     * True while the workspace is in the middle of switching back to gameplay. The Minecraft screen-redirect mixin
     * inspects this so the engine's own {@code setScreen(null)} call is allowed through instead of being trapped and
     * looped back into the workspace.
     */
    public static boolean isPreparingToClose() {
        return preparingToClose;
    }

    public static void setPreparingToClose(boolean value) {
        preparingToClose = value;
    }

    /**
     * True while the workspace is rendering its wrapped (vanilla) screen into an off-screen RT during the menu-overlay
     * mode. The blur-skip mixin reads this so vanilla's full-screen blur effect doesn't smear over the workspace.
     */
    public static boolean isInWrappedScreenRender() {
        return inWrappedScreenRender;
    }

    public static void setInWrappedScreenRender(boolean value) {
        inWrappedScreenRender = value;
    }
}

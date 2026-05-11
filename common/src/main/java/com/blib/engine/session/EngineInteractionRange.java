package com.blib.engine.session;

import org.jetbrains.annotations.ApiStatus;

/**
 * Single source of truth for the maximum raycast distance used by camera-relative engine interactions — placement
 * cursor, hover probing, click-to-select, orbit pivot. Sized large enough to reach the far edge of loaded chunks at any
 * practical render distance: Minecraft's {@code clip} short-circuits at the first visible hit, so a generous max only
 * costs the walk past unloaded chunks, which is fast.
 * <p>
 * The freecam camera detaches from the player, so reach must follow the camera rather than the player's vanilla
 * 4.5-block radius. A unified value here keeps "what you can click" / "what you can place" / "what you can orbit
 * around" coherent — earlier the constants drifted (96 for placement / hover / selection, 8192 for orbit), which
 * surfaced as "MMB-orbit works on that far cliff, but I can't click-select the block sitting on top of it."
 */
@ApiStatus.Internal
public final class EngineInteractionRange {

    public static final double MAX = 8192.0;

    public static final double MAX_SQR = MAX * MAX;

    private EngineInteractionRange() {}
}

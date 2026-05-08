package com.blib.internal.client.engine;

import org.jetbrains.annotations.ApiStatus;

/**
 * Engine-mode camera control scheme.
 * <ul>
 * <li>{@link #FLY} — spectator-style: WASD translation in the camera frame, mouse-look rotation, scroll inert.</li>
 * <li>{@link #ORBIT} — DCC-style: LMB-drag orbits around a pivot, RMB-drag pans, scroll zooms toward/away from pivot.
 * Pivot is set on each LMB-press via a screen-center raycast.</li>
 * </ul>
 */
@ApiStatus.Internal
public enum NavigationMode {
    FLY,
    ORBIT
}

package com.blib.internal.client.shader;

import org.jetbrains.annotations.ApiStatus;

/**
 * Shared on/off flag for thermal-vision mode. Read by the entity shader-substitution mixin and by the per-entity
 * batch-break mixin; written by {@code BLibThermalCommand}. Volatile because the command can run on the
 * integrated-server thread while the render-thread mixins read it.
 */
@ApiStatus.Internal
public final class BLibThermalState {

    private static volatile boolean active;

    private BLibThermalState() {
        throw new UnsupportedOperationException();
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        BLibThermalState.active = active;
    }
}

package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.service.BLibInternalClientServices;
import com.blib.mod.BLib;

/**
 * Loader-agnostic facade for "is a shader pack mod (Iris on Fabric / Oculus on NeoForge) loaded?" When the answer is
 * yes, BLib's post-effect pipeline disables itself entirely — the MRT-augmented main framebuffer is not created,
 * vanilla entity shaders are not patched, and the per-frame pipeline runs as a no-op. Iris owns the render pipeline at
 * that point and any further mucking on our side fights it.
 * <p>
 * The result is cached after the first call so that hot-path checks (mixin gates, per-frame pipeline) don't re-resolve
 * the loader's mod list on every frame.
 */
@ApiStatus.Internal
public final class BLibIrisCompat {

    private static volatile boolean checked;

    private static volatile boolean shaderModActive;

    private static volatile boolean warnedOnce;

    private BLibIrisCompat() {
        throw new UnsupportedOperationException();
    }

    public static boolean isShaderModActive() {
        if (!checked) {
            shaderModActive = BLibInternalClientServices.IRIS_COMPAT.isShaderModActive();
            checked = true;

            if (shaderModActive) {
                BLib.LOGGER.info("[BLib] Iris/Oculus detected; post-effects pipeline disabled.");
            }
        }

        return shaderModActive;
    }

    public static void warnDisabledOnce() {
        if (!warnedOnce && shaderModActive) {
            warnedOnce = true;
            BLib.LOGGER.warn("[BLib] Post-effect requested while a shader mod is active; effects skipped.");
        }
    }
}

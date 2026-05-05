package com.blib.internal.client.service;

import org.jetbrains.annotations.ApiStatus;

/**
 * Loader-agnostic check for "is a shader-pack mod (Iris on Fabric / Oculus on NeoForge / etc.) currently loaded?"
 * Result is consulted lazily by {@link com.blib.internal.client.posteffect.BLibIrisCompat} and cached after first call.
 */
@ApiStatus.Internal
public interface BLibClientIrisCompatService {

    boolean isShaderModActive();
}

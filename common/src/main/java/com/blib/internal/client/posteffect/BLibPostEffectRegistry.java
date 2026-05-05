package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.client.shader.v1.BLibPostEffect;
import com.blib.api.client.shader.v1.BLibPostEffectSpec;
import com.blib.mod.BLib;

/**
 * Common registry of post-effects. Loader-specific shader-registration glue iterates {@link #ALL} during shader load
 * and assigns each entry a {@code ShaderInstance}; the per-frame pipeline iterates the same list and runs the active
 * subset.
 */
@ApiStatus.Internal
public final class BLibPostEffectRegistry {

    private static final List<BLibPostEffectImpl> MUTABLE = new ArrayList<>();

    public static final List<BLibPostEffectImpl> ALL = Collections.unmodifiableList(MUTABLE);

    private BLibPostEffectRegistry() {
        throw new UnsupportedOperationException();
    }

    public static synchronized BLibPostEffect register(BLibClientMod mod, BLibPostEffectSpec spec) {
        for (var existing : MUTABLE) {
            if (existing.id().equals(spec.id())) {
                throw new IllegalStateException("Post-effect already registered: " + spec.id());
            }
        }

        var impl = new BLibPostEffectImpl(mod, spec);
        MUTABLE.add(impl);

        BLib.LOGGER.debug("Registered BLib post-effect '{}' for mod '{}'", spec.id(), mod.id());

        return impl;
    }
}

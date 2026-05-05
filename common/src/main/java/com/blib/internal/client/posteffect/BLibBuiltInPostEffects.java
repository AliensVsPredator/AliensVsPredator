package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.shader.v1.BLibPostEffectInput;
import com.blib.api.client.shader.v1.BLibPostEffectSpec;
import com.blib.internal.client.BLibClient;
import com.blib.internal.client.shader.BLibThermalState;
import com.blib.mod.BLib;

/**
 * BLib's own post-effects, registered via the public {@code BLib.CLIENT.postEffects().register(...)} API. Today this is
 * just the thermal effect; future BLib-shipped effects (debug overlays, etc.) go here too.
 * <p>
 * Calling through the public API from BLib's own client init doubles as a self-test: a working thermal effect implies
 * the public registration path, the loader-specific shader wiring, and the framework pipeline are all intact.
 */
@ApiStatus.Internal
public final class BLibBuiltInPostEffects {

    private BLibBuiltInPostEffects() {
        throw new UnsupportedOperationException();
    }

    public static void register() {
        BLibClient.MOD.postEffects()
            .register(
                BLibPostEffectSpec.builder(
                    BLib.MOD.resources().createLocation("thermal"),
                    BLib.MOD.resources().createLocation("blib_post/thermal")
                )
                    .withInput(BLibPostEffectInput.COLOR_TEXTURE)
                    .withInput(BLibPostEffectInput.DEPTH_TEXTURE)
                    .withInput(BLibPostEffectInput.ENTITY_MASK)
                    .withInput(BLibPostEffectInput.ENTITY_LIGHTMAP)
                    .enabledWhen(BLibThermalState::isActive)
                    .priority(100)
                    .build()
            );
    }
}

package com.blib.api.client.shader.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.client.shader.v1.BLibPostEffect;
import com.blib.api.client.shader.v1.BLibPostEffectSpec;
import com.blib.internal.client.posteffect.BLibPostEffectRegistry;

/**
 * Per-mod accessor for the post-effect framework. Reachable from a mod's {@link BLibClientMod#postEffects()}.
 * <p>
 * Registrations are stored in a single global registry; the per-mod accessor exists for context (debug logs, future
 * per-mod scoping) and to match BLib's other client API access patterns.
 */
public class BLibClientPostEffectAccess {

    private final BLibClientMod mod;

    @ApiStatus.Internal
    public BLibClientPostEffectAccess(BLibClientMod mod) {
        this.mod = mod;
    }

    /**
     * Register a post-effect. The returned handle reflects the effect's current active state (resolved from the spec's
     * {@code enabledWhen} supplier each frame). The actual GPU resource (a {@code ShaderInstance} for the effect's
     * fragment shader) is wired by the loader during the next resource-pack reload.
     */
    public BLibPostEffect register(BLibPostEffectSpec spec) {
        return BLibPostEffectRegistry.register(mod, spec);
    }
}

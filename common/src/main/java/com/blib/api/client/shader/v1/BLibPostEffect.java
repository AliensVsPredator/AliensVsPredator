package com.blib.api.client.shader.v1;

import net.minecraft.resources.ResourceLocation;

/**
 * Handle returned from {@link com.blib.api.client.shader.v1.model.access.BLibClientPostEffectAccess#register} that lets
 * a consumer introspect a registered effect. Active state is resolved each frame from the spec's {@code enabledWhen}
 * supplier — consumers don't toggle the effect through this handle, they wire a
 * {@link java.util.function.BooleanSupplier} that reads whatever upstream state should drive it.
 * <p>
 * Implementations are produced by the framework; consumers neither implement nor cast to subtypes.
 */
public interface BLibPostEffect {

    ResourceLocation id();

    boolean isActive();

    BLibPostEffectSpec spec();
}

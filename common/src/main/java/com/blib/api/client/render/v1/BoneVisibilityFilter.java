package com.blib.api.client.render.v1;

import com.blib.api.client.model.v1.AzBone;

/**
 * Renderer hook that decides per-frame whether a bone should be hidden.
 * <p>
 * Returning {@code true} hides the bone <em>and</em> its descendants for this render pass without mutating the bone's
 * persistent state. Used by the dismemberment system to make detached bones disappear without touching the entity's
 * per-instance baked model.
 */
@FunctionalInterface
public interface BoneVisibilityFilter<T> {

    boolean shouldHideBone(AzBone bone, T animatable);
}

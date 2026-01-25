package com.blib.azurelib.common.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The {@code AzAnimatorAccessor} interface provides a mechanism to associate and manage an
 * {@link com.blib.azurelib.common.animation.AzAnimator} instance with a target object. This enables retrieval and
 * manipulation of animator instances that are specific to the target object.
 *
 * @param <K> The type of the key used to identify the animatable object. Typically, a UUID for items/entities and Long
 *            for BlockEntities.
 * @param <T> The type of the target object that the animator applies to.
 */
public interface AzAnimatorAccessor<K, T> {

    @Nullable
    com.blib.azurelib.common.animation.AzAnimator<K, T> getAnimatorOrNull();

    void setAnimator(com.blib.azurelib.common.animation.AzAnimator<K, T> animator);

    default Optional<com.blib.azurelib.common.animation.AzAnimator<K, T>> getAnimator() {
        return Optional.ofNullable(getAnimatorOrNull());
    }

    @SuppressWarnings("unchecked")
    static <K, T> AzAnimatorAccessor<K, T> cast(T target) {
        return (AzAnimatorAccessor<K, T>) target;
    }

    static <K, T> com.blib.azurelib.common.animation.AzAnimator<K, T> getOrNull(T target) {
        return AzAnimatorAccessor.<K, T>cast(target).getAnimatorOrNull();
    }

    static <K, T> Optional<AzAnimator<K, T>> get(T target) {
        return Optional.ofNullable(getOrNull(target));
    }
}

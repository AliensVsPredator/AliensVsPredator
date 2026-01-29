package com.blib.azurelib.common.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AzAnimatorAccessor<K, T> {

    @Nullable
    AzAnimator<K, T> getAnimatorOrNull();

    void setAnimator(AzAnimator<K, T> animator);

    default Optional<AzAnimator<K, T>> getAnimator() {
        return Optional.ofNullable(getAnimatorOrNull());
    }

    @SuppressWarnings("unchecked")
    static <K, T> AzAnimatorAccessor<K, T> cast(T target) {
        return (AzAnimatorAccessor<K, T>) target;
    }

    static <K, T> AzAnimator<K, T> getOrNull(T target) {
        return AzAnimatorAccessor.<K, T>cast(target).getAnimatorOrNull();
    }

    static <K, T> Optional<AzAnimator<K, T>> get(T target) {
        return Optional.ofNullable(getOrNull(target));
    }
}

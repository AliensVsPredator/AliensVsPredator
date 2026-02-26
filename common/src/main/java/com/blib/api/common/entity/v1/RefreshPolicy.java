package com.blib.api.common.entity.v1;

@FunctionalInterface
public interface RefreshPolicy<T> {

    boolean shouldRefresh(T context);

    default RefreshPolicy<T> and(RefreshPolicy<T> other) {
        return context -> this.shouldRefresh(context) && other.shouldRefresh(context);
    }

    default RefreshPolicy<T> or(RefreshPolicy<T> other) {
        return context -> this.shouldRefresh(context) || other.shouldRefresh(context);
    }
}

package com.lib.common.gameplay.goap;

import com.just.core.functional.option.Option;
import com.just.goap.GOAP;
import org.jetbrains.annotations.Nullable;

public interface GOAPUser<T> {

    @Nullable
    GOAP<T> createGOAP();

    default @Nullable GOAP<T> getGOAPOrNull() {
        return null;
    }

    default Option<GOAP<T>> getGOAP() {
        return Option.ofNullable(getGOAPOrNull());
    }
}

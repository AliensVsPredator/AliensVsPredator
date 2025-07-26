package com.lib.common.gameplay.goap;

import com.bvanseg.just.functional.option.Option;
import org.jetbrains.annotations.Nullable;

public interface GOAPUser<T extends GOAP<?>> {

    @Nullable
    T createGOAP();

    default @Nullable T getGOAPOrNull() {
        return null;
    }

    default Option<T> getGOAP() {
        return Option.ofNullable(getGOAPOrNull());
    }
}

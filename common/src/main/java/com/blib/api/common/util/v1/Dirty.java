package com.blib.api.common.util.v1;

import org.jetbrains.annotations.ApiStatus;

public interface Dirty {

    void markDirty();

    boolean isDirty();

    @ApiStatus.Internal
    void clearDirty();
}

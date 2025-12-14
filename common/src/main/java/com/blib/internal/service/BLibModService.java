package com.blib.internal.service;

import com.blib.BLibMod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BLibModService {

    void postInitialize(BLibMod mod);
}

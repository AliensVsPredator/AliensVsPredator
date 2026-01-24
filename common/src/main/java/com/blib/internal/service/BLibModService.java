package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;

@ApiStatus.Internal
public interface BLibModService {

    void initialize(BLibMod mod, Runnable runnable);

    void postInitialize(BLibMod mod);
}

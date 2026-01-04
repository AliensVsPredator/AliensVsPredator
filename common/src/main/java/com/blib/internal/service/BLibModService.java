package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;

@ApiStatus.Internal
public interface BLibModService {

    void initialize(BLibMod mod, Runnable runnable);

    void postInitialize(BLibMod mod);
}

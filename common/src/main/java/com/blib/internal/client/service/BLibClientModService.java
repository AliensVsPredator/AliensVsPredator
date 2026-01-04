package com.blib.internal.client.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;

@ApiStatus.Internal
public interface BLibClientModService {

    void initialize(BLibClientMod mod, Runnable runnable);
}

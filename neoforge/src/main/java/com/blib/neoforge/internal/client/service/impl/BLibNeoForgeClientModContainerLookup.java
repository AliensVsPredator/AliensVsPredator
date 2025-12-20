package com.blib.neoforge.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.client.BLibClientMod;

@ApiStatus.Internal
public class BLibNeoForgeClientModContainerLookup {

    public static final BLibNeoForgeClientModContainerLookup INSTANCE = new BLibNeoForgeClientModContainerLookup();

    private final Map<BLibClientMod, BLibNeoForgeClientModContainer> modToContainerMap;

    private BLibNeoForgeClientModContainerLookup() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    public BLibNeoForgeClientModContainer get(BLibClientMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibNeoForgeClientModContainer(mod));
    }
}

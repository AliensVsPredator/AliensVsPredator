package com.blib.fabric.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.client.mod.v1.BLibClientMod;

@ApiStatus.Internal
public class BLibFabricClientModContainerLookup {

    public static final BLibFabricClientModContainerLookup INSTANCE = new BLibFabricClientModContainerLookup();

    private final Map<BLibClientMod, BLibFabricClientModContainer> modToContainerMap;

    private BLibFabricClientModContainerLookup() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    public BLibFabricClientModContainer get(BLibClientMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibFabricClientModContainer(mod));
    }
}

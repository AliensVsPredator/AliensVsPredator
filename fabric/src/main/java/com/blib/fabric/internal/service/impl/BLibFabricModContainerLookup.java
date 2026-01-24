package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.mod.v1.BLibMod;

@ApiStatus.Internal
public class BLibFabricModContainerLookup {

    public static final BLibFabricModContainerLookup INSTANCE = new BLibFabricModContainerLookup();

    private final Map<BLibMod, BLibFabricModContainer> modToContainerMap;

    private BLibFabricModContainerLookup() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    public BLibFabricModContainer get(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibFabricModContainer(mod));
    }
}

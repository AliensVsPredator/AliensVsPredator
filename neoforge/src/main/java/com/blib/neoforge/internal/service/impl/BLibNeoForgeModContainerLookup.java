package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.BLibMod;
import com.blib.common.registry.BLibHolder;

@ApiStatus.Internal
public class BLibNeoForgeModContainerLookup {

    public static final BLibNeoForgeModContainerLookup INSTANCE = new BLibNeoForgeModContainerLookup();

    private final Map<BLibMod, BLibNeoForgeModContainer> modToContainerMap;

    private BLibNeoForgeModContainerLookup() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    public BLibNeoForgeModContainer get(BLibHolder<?> holder) {
        return get(holder.getRegistry().getMod());
    }

    public BLibNeoForgeModContainer get(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibNeoForgeModContainer(mod));
    }
}

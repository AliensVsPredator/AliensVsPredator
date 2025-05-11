package com.avp.common.level.gameevent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPGameEvents {

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_ABERRANT_RESIN_SPREAD = register("aberrant_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_IRRADIATED_RESIN_SPREAD = register("irradiated_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_NETHER_RESIN_SPREAD = register("nether_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_RESIN_SPREAD = register("resin_spread");

    private static AVPDeferredHolder<GameEvent> register(String id) {
        return register(id, 16);
    }

    private static AVPDeferredHolder<GameEvent> register(String id, int radius) {
        return Services.REGISTRY.register(BuiltInRegistries.GAME_EVENT, id, () -> new GameEvent(radius));
    }

    public static void initialize() {}
}

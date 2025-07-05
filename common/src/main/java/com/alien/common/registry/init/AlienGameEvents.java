package com.alien.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AlienGameEvents {

    public static final AVPDeferredHolder<GameEvent> EGG_ABERRANT_PICKUP_REQUEST = register("egg_aberrant_pickup_request");

    public static final AVPDeferredHolder<GameEvent> EGG_NETHER_PICKUP_REQUEST = register("egg_nether_pickup_request");

    public static final AVPDeferredHolder<GameEvent> EGG_PICKUP_REQUEST = register("egg_pickup_request");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_ABERRANT_RESIN_SPREAD = register("aberrant_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_IRRADIATED_RESIN_SPREAD = register("irradiated_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_NETHER_RESIN_SPREAD = register("nether_resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_RESIN_SPREAD = register("resin_spread");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_ABERRANT_CRY_FOR_HELP = register("aberrant_cry_for_help");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_IRRADIATED_CRY_FOR_HELP = register("irradiated_cry_for_help");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_NETHER_CRY_FOR_HELP = register("nether_cry_for_help");

    public static final AVPDeferredHolder<GameEvent> XENOMORPH_CRY_FOR_HELP = register("cry_for_help");

    private static AVPDeferredHolder<GameEvent> register(String id) {
        return register(id, 16);
    }

    private static AVPDeferredHolder<GameEvent> register(String id, int radius) {
        return Services.REGISTRY.register(BuiltInRegistries.GAME_EVENT, id, () -> new GameEvent(radius));
    }

    public static void initialize() {}
}

package com.avp.common.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

import com.avp.service.Services;

public class AVPGameEvents {

    public static final Holder<GameEvent> XENOMORPH_RESIN_SPREAD = register("resin_spread");

    private static Holder<GameEvent> register(String id) {
        return register(id, 16);
    }

    private static Holder<GameEvent> register(String id, int radius) {
        return Services.REGISTRY.register(BuiltInRegistries.GAME_EVENT, id, () -> new GameEvent(radius));
    }

    public static void initialize() {}
}

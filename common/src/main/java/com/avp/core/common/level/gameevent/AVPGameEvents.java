package com.avp.core.common.level.gameevent;

import com.avp.core.platform.service.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

import com.avp.core.AVPResources;

import java.util.function.Supplier;

public class AVPGameEvents {

    public static final Supplier<GameEvent> XENOMORPH_RESIN_SPREAD = register("resin_spread");

    private static Supplier<GameEvent> register(String id) {
        return register(id, 16);
    }

    private static Supplier<GameEvent>  register(String id, int radius) {
        return Services.REGISTRY.registerGameEvent(id, () -> new GameEvent(radius));
    }

    public static void initialize() {}
}

package com.avp.common.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

import com.avp.AVPResources;

public class AVPGameEvents {

    public static final Holder.Reference<GameEvent> XENOMORPH_RESIN_SPREAD = register("resin_spread");

    private static Holder.Reference<GameEvent> register(String id) {
        return register(id, 16);
    }

    private static Holder.Reference<GameEvent> register(String id, int radius) {
        var resourceLocation = AVPResources.location(id);
        var gameEvent = new GameEvent(radius);
        return Registry.registerForHolder(BuiltInRegistries.GAME_EVENT, resourceLocation, gameEvent);
    }

    public static void initialize() {}
}

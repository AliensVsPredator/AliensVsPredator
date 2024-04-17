package org.avp.neoforge.common.registry;

import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.Objects;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPFuelRegistry;

public class AVPNeoForgeFuelRegistry {

    public static void handleFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        AVPFuelRegistry.INSTANCE.getEntries()
            .stream()
            .map(GameObject::get)
            .filter(tuple -> Objects.equals(tuple.first().get(), event.getItemStack().getItem()))
            .findFirst()
            .ifPresent(tuple -> event.setBurnTime(tuple.second()));
    }

    private AVPNeoForgeFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}

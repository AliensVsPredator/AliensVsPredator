package org.avp.neoforge.common.registry;

import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import org.avp.common.registry.AVPFuelRegistry;

import java.util.Objects;

public class AVPNeoForgeFuelRegistry {

    public static void handleFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        AVPFuelRegistry.getEntries().stream()
            .filter(tuple -> Objects.equals(tuple.first().get(), event.getItemStack().getItem()))
            .findFirst()
            .ifPresent(tuple -> event.setBurnTime(tuple.second()));
    }

    private AVPNeoForgeFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}

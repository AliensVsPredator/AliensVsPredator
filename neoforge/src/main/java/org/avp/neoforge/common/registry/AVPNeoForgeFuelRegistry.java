package org.avp.neoforge.common.registry;

import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.Objects;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.AVPFuelRegistry;

public class AVPNeoForgeFuelRegistry {

    public static void handleFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        AVPFuelRegistry.INSTANCE.getValues()
            .stream()
            .map(BLHolder::get)
            .filter(entry -> Objects.equals(entry.getKey().get(), event.getItemStack().getItem()))
            .findFirst()
            .ifPresent(entry -> event.setBurnTime(entry.getValue()));
    }

    private AVPNeoForgeFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}

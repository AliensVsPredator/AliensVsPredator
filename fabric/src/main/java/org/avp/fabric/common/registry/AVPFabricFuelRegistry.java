package org.avp.fabric.common.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;

import org.avp.api.Holder;
import org.avp.common.registry.AVPFuelRegistry;

public class AVPFabricFuelRegistry {

    public static void register() {
        AVPFuelRegistry.INSTANCE.getEntries().stream()
            .map(Holder::get)
            .forEach(tuple -> FuelRegistry.INSTANCE.add(tuple.first().get(), tuple.second()));
    }

    private AVPFabricFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}

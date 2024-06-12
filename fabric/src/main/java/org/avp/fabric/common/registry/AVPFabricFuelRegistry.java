package org.avp.fabric.common.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.AVPFuelRegistry;

public class AVPFabricFuelRegistry {

    public static void register() {
        AVPFuelRegistry.INSTANCE.getValues()
            .stream()
            .map(BLHolder::get)
            .forEach(tuple -> FuelRegistry.INSTANCE.add(tuple.first().get(), tuple.second()));
    }

    private AVPFabricFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}

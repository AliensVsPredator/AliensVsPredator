package com.avp.common.fuel;

import net.fabricmc.fabric.api.registry.FuelRegistry;

import com.avp.common.item.AVPItems;

public class AVPFuelRegistry {

    public static void initialize() {
        FuelRegistry.INSTANCE.add(AVPItems.CARBON_DUST, 800);
    }
}

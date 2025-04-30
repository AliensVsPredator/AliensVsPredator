package com.avp.fabric.common.fuel;

import net.fabricmc.fabric.api.registry.FuelRegistry;

import com.avp.fabric.common.item.AVPItems;

public class AVPFuelRegistry {

    public static void initialize() {
        FuelRegistry.INSTANCE.add(AVPItems.CARBON_DUST, 800);
    }
}

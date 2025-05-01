package com.avp.fabric.common.fuel;

import net.fabricmc.fabric.api.registry.FuelRegistry;

import com.avp.common.item.TempAVPItems;

public class AVPFuelRegistry {

    public static void initialize() {
        FuelRegistry.INSTANCE.add(TempAVPItems.CARBON_DUST.get(), 800);
    }
}

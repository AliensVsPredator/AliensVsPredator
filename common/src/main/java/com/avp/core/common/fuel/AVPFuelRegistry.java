package com.avp.core.common.fuel;

import com.avp.core.platform.service.Services;

import com.avp.core.common.item.AVPItems;

public class AVPFuelRegistry {

    public static void initialize() {
        Services.REGISTRY.registerFuel(AVPItems.CARBON_DUST, 800);
    }
}

package com.avp.common.fuel;

import com.avp.common.item.AVPItems;
import com.avp.service.Services;

public class AVPFuelRegistry {

    public static void initialize() {
        Services.REGISTRY.registerFurnaceFuel(AVPItems.CARBON_DUST, 800);
    }
}

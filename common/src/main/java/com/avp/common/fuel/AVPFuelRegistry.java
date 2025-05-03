package com.avp.common.fuel;

import com.avp.common.item.TempAVPItems;
import com.avp.service.Services;

public class AVPFuelRegistry {

    public static void initialize() {
        Services.REGISTRY.registerFurnaceFuel(TempAVPItems.CARBON_DUST, 800);
    }
}

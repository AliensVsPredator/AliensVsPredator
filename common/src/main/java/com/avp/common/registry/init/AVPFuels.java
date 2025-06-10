package com.avp.common.registry.init;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.service.Services;

public class AVPFuels {

    public static void initialize() {
        Services.REGISTRY.registerFurnaceFuel(AVPItems.CARBON_DUST, 800);
    }
}

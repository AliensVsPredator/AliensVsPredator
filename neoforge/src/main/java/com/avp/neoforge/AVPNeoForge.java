package com.avp.neoforge;

import com.avp.AVP;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(AVP.MOD_ID)
public class AVPNeoForge {

    public AVPNeoForge(IEventBus modBus) {
        var registryService = (NeoForgeRegistryService) Services.REGISTRY;
        registryService.initialize(modBus);
    }

}

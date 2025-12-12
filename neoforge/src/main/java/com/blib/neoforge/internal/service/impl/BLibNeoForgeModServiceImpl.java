package com.blib.neoforge.internal.service.impl;

import com.blib.BLib;
import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;
import com.blib.neoforge.service.impl.NeoForgeBLibEventServiceImpl;
import com.blib.service.BLibServices;
import net.neoforged.fml.ModList;

public class BLibNeoForgeModServiceImpl implements BLibModService {

    @Override
    public void postInitialize(BLibMod mod) {
        var event = ((NeoForgeBLibEventServiceImpl) BLibServices.EVENT);
        var registry = (NeoForgeBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;
        var modContainerOptional = ModList.get().getModContainerById(mod.getId());

        if (modContainerOptional.isEmpty()) {
            BLib.LOGGER.warn("Unable to post-initialize mod '{}'. No NeoForge mod container was found for the mod.", mod.getId());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            registry.finalize(mod, modContainer.getEventBus());
        } else {
            BLib.LOGGER.warn("Unable to finalize registration for mod '{}' because its event bus is null.", mod.getId());
        }

        event.finalize(mod);
    }
}

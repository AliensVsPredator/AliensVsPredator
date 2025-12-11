package com.blib.neoforge.internal.service.impl;

import com.blib.BLib;
import com.blib.BLibMod;
import com.blib.internal.service.BLibModService;
import com.blib.neoforge.service.impl.NeoForgeBLibEventServiceImpl;
import com.blib.neoforge.service.impl.NeoForgeBLibRegistryServiceImpl;
import com.blib.service.BLibServices;
import net.neoforged.fml.ModList;

public class BLibNeoForgeModServiceImpl implements BLibModService {

    private static final NeoForgeBLibEventServiceImpl EVENT = ((NeoForgeBLibEventServiceImpl) BLibServices.EVENT);

    private static final NeoForgeBLibRegistryServiceImpl REGISTRY = (NeoForgeBLibRegistryServiceImpl) BLibServices.REGISTRY;

    @Override
    public void postInitialize(BLibMod mod) {
        var modContainerOptional = ModList.get().getModContainerById(mod.getId());

        if (modContainerOptional.isEmpty()) {
            BLib.LOGGER.warn("Unable to post-initialize mod '{}'. No NeoForge mod container was found for the mod.", mod.getId());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            REGISTRY.finalize(mod, modContainer.getEventBus());
        } else {
            BLib.LOGGER.warn("Unable to finalize registration for mod '{}' because its event bus is null.", mod.getId());
        }

        EVENT.finalize(mod);
    }
}

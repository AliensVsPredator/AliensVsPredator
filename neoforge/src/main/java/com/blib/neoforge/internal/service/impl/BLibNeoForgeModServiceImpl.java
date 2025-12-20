package com.blib.neoforge.internal.service.impl;

import net.neoforged.fml.ModList;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;

@ApiStatus.Internal
public class BLibNeoForgeModServiceImpl implements BLibModService {

    @Override
    public void postInitialize(BLibMod mod) {
        var registry = (BLibNeoForgeRegistryServiceImpl) BLibInternalServices.REGISTRY;
        var modContainerOptional = ModList.get().getModContainerById(mod.id());

        if (modContainerOptional.isEmpty()) {
            BLib.LOGGER.warn("Unable to post-initialize mod '{}'. No NeoForge mod container was found for the mod.", mod.id());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            registry.initialize(mod, eventBus);
        } else {
            BLib.LOGGER.warn("Unable to finalize registration for mod '{}' because its event bus is null.", mod.id());
        }
    }
}

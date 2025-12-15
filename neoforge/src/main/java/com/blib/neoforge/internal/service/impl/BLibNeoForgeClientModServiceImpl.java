package com.blib.neoforge.internal.service.impl;

import net.neoforged.fml.ModList;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.client.BLibClientMod;
import com.blib.internal.service.BLibClientModService;
import com.blib.internal.service.BLibInternalServices;

@ApiStatus.Internal
public class BLibNeoForgeClientModServiceImpl implements BLibClientModService {

    @Override
    public void initialize(BLibClientMod mod) {
        var registry = (BLibNeoForgeClientRegistryServiceImpl) BLibInternalServices.CLIENT_REGISTRY;
        var modContainerOptional = ModList.get().getModContainerById(mod.id());

        if (modContainerOptional.isEmpty()) {
            BLib.LOGGER.warn("Unable to initialize client mod '{}'. No NeoForge mod container was found for the mod.", mod.id());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            registry.finalize(mod, eventBus);
        } else {
            BLib.LOGGER.warn("Unable to finalize registration for client mod '{}' because its event bus is null.", mod.id());
        }
    }
}

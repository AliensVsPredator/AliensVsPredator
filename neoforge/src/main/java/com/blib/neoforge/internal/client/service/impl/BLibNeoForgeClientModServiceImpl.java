package com.blib.neoforge.internal.client.service.impl;

import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.client.BLibClientMod;
import com.blib.internal.client.service.BLibClientModService;
import com.blib.internal.client.service.BLibInternalClientServices;

@ApiStatus.Internal
public class BLibNeoForgeClientModServiceImpl implements BLibClientModService {

    @Override
    public void initialize(BLibClientMod mod, Runnable runnable) {
        var modContainerOptional = ModList.get().getModContainerById(mod.id());

        if (modContainerOptional.isEmpty()) {
            BLib.LOGGER.warn("Unable to initialize client mod '{}'. No NeoForge mod container was found for the mod.", mod.id());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            eventBus.<FMLConstructModEvent>addListener(event -> {
                var registry = (BLibNeoForgeClientRegistryServiceImpl) BLibInternalClientServices.CLIENT_REGISTRY;

                runnable.run();
                registry.initialize(mod, eventBus);
            });
        } else {
            BLib.LOGGER.warn("Unable to finalize registration for client mod '{}' because its event bus is null.", mod.id());
        }
    }
}

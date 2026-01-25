package com.blib.neoforge.internal.service.impl;

import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;

@ApiStatus.Internal
public class BLibNeoForgeModServiceImpl implements BLibModService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibNeoForgeModServiceImpl.class);

    @Override
    public void initialize(BLibMod mod, Runnable runnable) {
        var modContainerOptional = ModList.get().getModContainerById(mod.id());

        if (modContainerOptional.isEmpty()) {
            LOGGER.warn("Unable to initialize mod '{}'. No NeoForge mod container was found for the mod.", mod.id());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            eventBus.<FMLConstructModEvent>addListener(event -> runnable.run());
        } else {
            LOGGER.warn("Unable to run initialization for mod '{}' because its event bus is null.", mod.id());
        }
    }

    @Override
    public void postInitialize(BLibMod mod) {
        var registry = (BLibNeoForgeRegistryServiceImpl) BLibInternalServices.REGISTRY;
        var modContainerOptional = ModList.get().getModContainerById(mod.id());

        if (modContainerOptional.isEmpty()) {
            LOGGER.warn("Unable to post-initialize mod '{}'. No NeoForge mod container was found for the mod.", mod.id());
            return;
        }

        var modContainer = modContainerOptional.get();

        var eventBus = modContainer.getEventBus();

        if (eventBus != null) {
            registry.initialize(mod, eventBus);
        } else {
            LOGGER.warn("Unable to finalize registration for mod '{}' because its event bus is null.", mod.id());
        }
    }
}

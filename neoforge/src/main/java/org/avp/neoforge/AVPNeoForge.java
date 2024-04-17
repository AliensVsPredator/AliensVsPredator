package org.avp.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import org.avp.common.AVPCommon;
import org.avp.common.AVPConstants;
import org.avp.neoforge.client.AVPNeoForgeClient;
import org.avp.neoforge.common.command.AVPNeoForgeCommands;
import org.avp.neoforge.common.data.AVPNeoForgeData;
import org.avp.neoforge.common.entity.AVPNeoForgeEntitySpawns;
import org.avp.neoforge.common.registry.AVPNeoForgeFuelRegistry;
import org.avp.neoforge.service.*;
import org.avp.neoforge.service.NeoForgeNetworkPayloadHandlerRegistry;

@Mod(AVPConstants.MOD_ID)
public class AVPNeoForge {

    public AVPNeoForge(IEventBus eventBus) {
        AVPCommon.init();

        eventBus.addListener(AVPNeoForgeData::handleGatherDataEvent);
        eventBus.addListener(AVPNeoForgeEntitySpawns::handleSpawnPlacementRegisterEvent);
        eventBus.addListener(NeoForgeNetworkPayloadHandlerRegistry::registerPayloadHandlers);

        NeoForgeCreativeModeTabRegistry.CREATIVE_MODE_TABS.register(eventBus);
        NeoForgeItemRegistry.ITEMS.register(eventBus);
        NeoForgeBlockService.BLOCKS.register(eventBus);
        NeoForgeEntityRegistry.ENTITY_TYPES.register(eventBus);
        NeoForgeParticleRegistry.PARTICLE_TYPES.register(eventBus);
        NeoForgeSoundEventService.SOUND_EVENTS.register(eventBus);
        eventBus.addListener(NeoForgeEntityAttributeRegistry.getInstance()::createEntityAttributes);

        NeoForge.EVENT_BUS.addListener(AVPNeoForgeCommands::registerCommandsEvent);
        NeoForge.EVENT_BUS.addListener(AVPNeoForgeFuelRegistry::handleFurnaceFuelBurnTimeEvent);

        // Client events
        NeoForge.EVENT_BUS.addListener(AVPNeoForgeClient::onClientTick);
    }
}

package org.avp.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import org.avp.common.AVPCommon;
import org.avp.common.AVPConstants;
import org.avp.neoforge.common.command.AVPNeoForgeCommands;
import org.avp.neoforge.service.*;
import org.avp.neoforge.service.NeoForgeNetworkPayloadHandlerRegistry;

/**
 * @author Boston Vanseghi
 */
@Mod(AVPConstants.MOD_ID)
public class AVPNeoForge {

    public AVPNeoForge(IEventBus eventBus) {
        AVPCommon.init();

        eventBus.addListener(NeoForgeNetworkPayloadHandlerRegistry::registerPayloadHandlers);

        NeoForgeCreativeModeTabRegistry.CREATIVE_MODE_TABS.register(eventBus);
        NeoForgeItemRegistry.ITEMS.register(eventBus);
        NeoForgeBlockRegistry.BLOCKS.register(eventBus);
        NeoForgeEntityRegistry.ENTITY_TYPES.register(eventBus);
        NeoForgeSoundEventRegistry.SOUND_EVENTS.register(eventBus);
        eventBus.addListener(NeoForgeEntityAttributeRegistry.getInstance()::createEntityAttributes);

        NeoForge.EVENT_BUS.addListener(AVPNeoForgeCommands::registerCommandsEvent);
    }
}

package org.avp.common.service;

import java.util.ServiceLoader;

import org.avp.common.AVPConstants;

public class Services {

    public static final BlockRegistry BLOCK_REGISTRY = load(BlockRegistry.class);

    public static final CreativeModeTabRegistry CREATIVE_MODE_TAB_REGISTRY = load(CreativeModeTabRegistry.class);

    public static final EntityRegistry ENTITY_REGISTRY = load(EntityRegistry.class);

    public static final ItemRegistry ITEM_REGISTRY = load(ItemRegistry.class);

    public static final NetworkHandler NETWORK_HANDLER = load(NetworkHandler.class);

    public static final NetworkPayloadHandlerRegistry PAYLOAD_HANDLER_REGISTRY = load(NetworkPayloadHandlerRegistry.class);

    public static final SoundEventRegistry SOUND_EVENT_REGISTRY = load(SoundEventRegistry.class);

    public static final Platform PLATFORM = load(Platform.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AVPConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    private Services() {
        throw new UnsupportedOperationException();
    }
}

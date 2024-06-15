package org.avp.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Services {

    private static final Logger LOGGER = LoggerFactory.getLogger(Services.class);

    public static final BlockEntityTypeService BLOCK_ENTITY_TYPE_SERVICE = load(BlockEntityTypeService.class);

    public static final BlockService BLOCK_SERVICE = load(BlockService.class);

    public static final CreativeModeTabService CREATIVE_MODE_TAB_SERVICE = load(CreativeModeTabService.class);

    public static final EntityTypeService ENTITY_TYPE_SERVICE = load(EntityTypeService.class);

    public static final ItemService ITEM_SERVICE = load(ItemService.class);

    public static final NetworkService NETWORK_SERVICE = load(NetworkService.class);

    public static final ParticleProviderService PARTICLE_PROVIDER_SERVICE = load(ParticleProviderService.class);

    public static final ParticleTypeService PARTICLE_TYPE_SERVICE = load(ParticleTypeService.class);

    public static final NetworkPayloadService NETWORK_PAYLOAD_SERVICE = load(NetworkPayloadService.class);

    public static final SoundEventService SOUND_EVENT_SERVICE = load(SoundEventService.class);

    public static final PlatformService PLATFORM_SERVICE = load(PlatformService.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    private Services() {
        throw new UnsupportedOperationException();
    }
}

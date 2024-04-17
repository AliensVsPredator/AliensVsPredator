package org.avp.common;

import mod.azure.azurelib.common.internal.common.AzureLib;

import org.avp.client.render.particle.AVPParticleTypes;
import org.avp.common.block.*;
import org.avp.common.config.AVPConfig;
import org.avp.common.creative_tab.AVPCreativeModeTabs;
import org.avp.common.entity.spawn.AVPEntitySpawns;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPEngineerEntityTypes;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.type.AVPYautjaEntityTypes;
import org.avp.common.item.*;
import org.avp.common.network.AVPNetworkPayloadHandlerRegistry;
import org.avp.common.registry.AVPFuelRegistry;
import org.avp.common.sound.AVPSoundEvents;

public class AVPCommon {

    public static void init() {
        // Initialize AzureLib.
        AzureLib.initialize();

        // AVPConfig initialization.
        AVPConfig.init();

        // Networking
        AVPNetworkPayloadHandlerRegistry.forceInitialization();

        // Fuel
        AVPFuelRegistry.forceInitialization();

        // Items
        AVPAmmunitionPartItems.INSTANCE.register();
        AVPArmorItems.INSTANCE.register();
        AVPBulletItems.INSTANCE.register();
        AVPElectronicItems.INSTANCE.register();
        AVPFoodItems.INSTANCE.register();
        AVPItems.INSTANCE.register();
        AVPToolItems.INSTANCE.register();
        AVPWeaponBlueprintItems.INSTANCE.register();
        AVPWeaponItems.INSTANCE.register();
        AVPWeaponPartItems.INSTANCE.register();

        // Blocks
        AVPAlienBlocks.INSTANCE.register();
        AVPBlocks.INSTANCE.register();
        AVPEngineerBlocks.INSTANCE.register();
        AVPIndustrialBlocks.INSTANCE.register();
        AVPOreBlocks.INSTANCE.register();
        AVPPaddingBlocks.INSTANCE.register();
        AVPParadiseBlocks.INSTANCE.register();
        AVPTempleBlocks.INSTANCE.register();
        AVPUnidentifiedBlocks.INSTANCE.register();
        AVPYautjaShipBlocks.INSTANCE.register();

        // Item Blocks
        AVPItemBlockItems.INSTANCE.register();

        // Sounds
        AVPSoundEvents.INSTANCE.register();

        // Entities
        AVPBaseAlienEntityTypes.forceInitialization();
        AVPEngineerEntityTypes.forceInitialization();
        AVPEntityTypes.forceInitialization();
        AVPExoticAlienEntityTypes.forceInitialization();
        AVPPrometheusAlienEntityTypes.forceInitialization();
        AVPRunnerAlienEntityTypes.forceInitialization();
        AVPYautjaEntityTypes.forceInitialization();
        AVPEntitySpawns.forceInitialization();

        // Spawn Egg Items
        AVPSpawnEggItems.INSTANCE.register();

        // Particles
        AVPParticleTypes.forceInitialization();

        // It's important to register creative mode tabs last, as entities generate spawn eggs automatically.
        AVPCreativeModeTabs.register();
    }

    private AVPCommon() {
        throw new UnsupportedOperationException();
    }
}

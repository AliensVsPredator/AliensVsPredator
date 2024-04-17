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
        AVPAmmunitionPartItems.forceInitialization();
        AVPArmorItems.forceInitialization();
        AVPBulletItems.forceInitialization();
        AVPElectronicItems.forceInitialization();
        AVPFoodItems.forceInitialization();
        AVPItems.forceInitialization();
        AVPToolItems.forceInitialization();
        AVPWeaponBlueprintItems.forceInitialization();
        AVPWeaponItems.forceInitialization();
        AVPWeaponPartItems.forceInitialization();

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

        // Entities
        AVPBaseAlienEntityTypes.forceInitialization();
        AVPEngineerEntityTypes.forceInitialization();
        AVPEntityTypes.forceInitialization();
        AVPExoticAlienEntityTypes.forceInitialization();
        AVPPrometheusAlienEntityTypes.forceInitialization();
        AVPRunnerAlienEntityTypes.forceInitialization();
        AVPYautjaEntityTypes.forceInitialization();
        AVPEntitySpawns.forceInitialization();

        // Particles
        AVPParticleTypes.forceInitialization();

        // It's important to register creative mode tabs last, as entities generate spawn eggs automatically.
        AVPCreativeModeTabs.register();
    }

    private AVPCommon() {
        throw new UnsupportedOperationException();
    }
}
